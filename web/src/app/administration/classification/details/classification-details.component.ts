import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';

import { ClassificationDefinition } from 'app/models/classification-definition';
import { ACTION } from 'app/models/action';
import { Classification } from 'app/models/classification';
import { ErrorModel } from 'app/models/modal-error';
import { AlertModel, AlertType } from 'app/models/alert';

import { TaskanaDate } from 'app/shared/util/taskana.date';

import { ClassificationsService } from 'app/administration/services/classifications/classifications.service';
import { MasterAndDetailService } from 'app/services/masterAndDetail/master-and-detail.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { AlertService } from 'app/services/alert/alert.service';
import { TreeService } from 'app/services/tree/tree.service';
import { ClassificationTypesService } from 'app/administration/services/classification-types/classification-types.service';
// tslint:disable:max-line-length
import { ClassificationCategoriesService } from 'app/administration/services/classification-categories-service/classification-categories.service';
// tslint:enable:max-line-length
import { DomainService } from 'app/services/domain/domain.service';

@Component({
  selector: 'taskana-classification-details',
  templateUrl: './classification-details.component.html',
  styleUrls: ['./classification-details.component.scss']
})
export class ClassificationDetailsComponent implements OnInit, OnDestroy {

  classification: ClassificationDefinition;
  classificationClone: ClassificationDefinition;
  selectedId: string = undefined;
  showDetail = false;
  classificationTypes: Array<string> = [];
  badgeMessage = '';
  requestInProgress = false;
  categories: Array<string> = [];
  categorySelected: string;
  private action: any;
  private classificationServiceSubscription: Subscription;
  private classificationSelectedSubscription: Subscription;
  private routeSubscription: Subscription;
  private masterAndDetailSubscription: Subscription;
  private classificationSavingSubscription: Subscription;
  private classificationRemoveSubscription: Subscription;
  private selectedClassificationSubscription: Subscription;
  private categoriesSubscription: Subscription;
  private domainSubscription: Subscription;

  constructor(private classificationsService: ClassificationsService,
    private route: ActivatedRoute,
    private router: Router,
    private masterAndDetailService: MasterAndDetailService,
    private errorModalService: ErrorModalService,
    private requestInProgressService: RequestInProgressService,
    private alertService: AlertService,
    private treeService: TreeService,
    private classificationTypeService: ClassificationTypesService,
    private categoryService: ClassificationCategoriesService,
    private domainService: DomainService) { }

  ngOnInit() {
    this.classificationTypeService.getClassificationTypes().subscribe((classificationTypes: Array<string>) => {
      this.classificationTypes = classificationTypes;
    })
    this.classificationSelectedSubscription = this.classificationsService.getSelectedClassification()
      .subscribe(classificationIdSelected => {
        this.classification = undefined;
        if (classificationIdSelected) {
          this.fillClassificationInformation(classificationIdSelected);
        }
      });

    this.routeSubscription = this.route.params.subscribe(params => {
      let id = params['id'];
      this.action = undefined;
      if (id && id.indexOf('new-classification') !== -1) {
        this.action = ACTION.CREATE;
        this.badgeMessage = 'Creating new classification';
        id = id.replace('new-classification/', '');
        if (id === 'undefined') {
          id = undefined;
        }
        this.fillClassificationInformation(id);
      }

      if (id && id !== '') {
        this.selectClassification(id);
      }
    });

    this.masterAndDetailSubscription = this.masterAndDetailService.getShowDetail().subscribe(showDetail => {
      this.showDetail = showDetail;
    });


    this.categoriesSubscription = this.categoryService.getCategories().subscribe((categories: Array<string>) => {
      this.categories = categories;
      if (categories.length > 0 && this.classification) {
        this.classification.category = categories[0];
      }
    });
  }

  backClicked(): void {
    this.classificationsService.selectClassification(undefined);
    this.router.navigate(['./'], { relativeTo: this.route.parent });
  }

  selectType(type: string) {
    this.classification.type = type;
  }

  removeClassification() {
    if (!this.classification || !this.classification.classificationId) {
      this.errorModalService.triggerError(
        new ErrorModel('There is no classification selected', 'Please check if you are creating a classification'));
      return false;
    }
    this.requestInProgressService.setRequestInProgress(true);
    this.treeService.setRemovedNodeId(this.classification.classificationId);

    this.classificationRemoveSubscription = this.classificationsService
      .deleteClassification(this.classification._links.self.href)
      .subscribe(() => {
        const key = this.classification.key;
        this.classification = undefined;
        this.afterRequest();
        this.classificationsService.selectClassification(undefined);
        this.router.navigate(['administration/classifications']);
        this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, `Classification ${key} was removed successfully`))
      }, error => {
        this.errorModalService.triggerError(new ErrorModel('There was error while removing your classification', error))
        this.afterRequest();
      })
  }

  onSave() {
    this.requestInProgressService.setRequestInProgress(true);
    if (this.action === ACTION.CREATE) {
      this.classificationSavingSubscription = this.classificationsService.postClassification(this.classification)
        .subscribe((classification: ClassificationDefinition) => {
          this.classification = classification;
          this.classificationsService.selectClassification(classification.classificationId);
          this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, `Classification ${classification.key} was saved successfully`));
          this.afterRequest();
        },
          error => {
            this.errorModalService.triggerError(new ErrorModel('There was an error creating a classification', error))
            this.afterRequest();
          });
    } else {
      this.classificationSavingSubscription = this.classificationsService
        .putClassification(this.classification._links.self.href, this.classification)
        .subscribe((classification: ClassificationDefinition) => {
          this.classification = classification;
          this.afterRequest();
          this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, `Classification ${classification.key} was saved successfully`));
        }, error => {
          this.errorModalService.triggerError(new ErrorModel('There was error while saving your classification', error))
          this.afterRequest();
        })
    }
  }

  onClear() {
    this.alertService.triggerAlert(new AlertModel(AlertType.INFO, 'Reset edited fields'))
    this.classification = { ...this.classificationClone };
  }


  selectCategory(category: string) {
    this.classification.category = category;
  }

  private afterRequest() {
    this.requestInProgressService.setRequestInProgress(false);
    this.classificationsService.triggerClassificationSaved();
  }

  private selectClassification(id: string) {
    this.selectedId = id;
    this.classificationsService.selectClassification(id);
  }

  private fillClassificationInformation(classificationIdSelected: string) {
    if (this.action === ACTION.CREATE) { // CREATE
      this.initClassificationCreation(classificationIdSelected);
    } else {
      this.requestInProgress = true;
      this.classificationServiceSubscription = this.classificationsService.getClassification(classificationIdSelected)
        .subscribe((classification: ClassificationDefinition) => {
          this.classification = classification;
          this.classificationClone = { ...this.classification };
          this.requestInProgress = false;
          this.checkDomainAndRedirect();
        });
    }
  }

  private addDateToClassification() {
    const date = TaskanaDate.getDate();
    this.classification.created = date;
    this.classification.modified = date;
  }

  private initClassificationCreation(classificationIdSelected: string) {
    this.classification = new ClassificationDefinition();
    this.selectedClassificationSubscription = this.classificationTypeService.getSelectedClassificationType().subscribe(value => {
      if (this.classification) { this.classification.type = value; }
    });
    this.classification.domain = this.domainService.getSelectedDomainValue();
    this.classification.category = this.categories[0];
    this.addDateToClassification();
    this.classification.parentId = classificationIdSelected;
    this.classificationClone = { ...this.classification };
  }

  private checkDomainAndRedirect() {
    this.domainSubscription = this.domainService.getSelectedDomain().subscribe(domain => {
      if (this.classification && this.classification.domain !== domain) {
        this.backClicked();
      }
    });
  }

  ngOnDestroy(): void {

    if (this.masterAndDetailSubscription) { this.masterAndDetailSubscription.unsubscribe(); }
    if (this.routeSubscription) { this.routeSubscription.unsubscribe(); }
    if (this.classificationSelectedSubscription) { this.classificationSelectedSubscription.unsubscribe(); }
    if (this.classificationServiceSubscription) { this.classificationServiceSubscription.unsubscribe(); }
    if (this.classificationSavingSubscription) { this.classificationSavingSubscription.unsubscribe(); }
    if (this.classificationRemoveSubscription) { this.classificationRemoveSubscription.unsubscribe(); }
    if (this.selectedClassificationSubscription) { this.selectedClassificationSubscription.unsubscribe(); }
    if (this.categoriesSubscription) { this.categoriesSubscription.unsubscribe(); }
    if (this.domainSubscription) { this.domainSubscription.unsubscribe(); }
  }
}
