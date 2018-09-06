import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { ClassificationDefinition } from 'app/models/classification-definition';
import { ACTION } from 'app/models/action';
import { ErrorModel } from 'app/models/modal-error';
import { AlertModel, AlertType } from 'app/models/alert';

import { highlight } from 'app/shared/animations/validation.animation';
import { TaskanaDate } from 'app/shared/util/taskana.date';

import { ClassificationsService } from 'app/administration/services/classifications/classifications.service';
import { MasterAndDetailService } from 'app/services/masterAndDetail/master-and-detail.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { AlertService } from 'app/services/alert/alert.service';
import { TreeService } from 'app/services/tree/tree.service';
import { RemoveConfirmationService } from 'app/services/remove-confirmation/remove-confirmation.service';

// tslint:disable:max-line-length
import { ClassificationCategoriesService } from 'app/administration/services/classification-categories-service/classification-categories.service';
// tslint:enable:max-line-length
import { DomainService } from 'app/services/domain/domain.service';
import { CustomFieldsService } from '../../../services/custom-fields/custom-fields.service';
import { Pair } from 'app/models/pair';
import { NgForm } from '@angular/forms';
import { FormsValidatorService } from 'app/shared/services/forms/forms-validator.service';

@Component({
  selector: 'taskana-classification-details',
  templateUrl: './classification-details.component.html',
  animations: [highlight],
  styleUrls: ['./classification-details.component.scss']
})
export class ClassificationDetailsComponent implements OnInit, OnDestroy {

  classification: ClassificationDefinition;
  classificationClone: ClassificationDefinition;
  selectedClassification: ClassificationDefinition = undefined;
  showDetail = false;
  classificationTypes: Array<string> = [];
  badgeMessage = '';
  requestInProgress = false;
  categories: Array<string> = [];
  categorySelected: string;
  spinnerIsRunning = false;
  custom1Field = this.customFieldsService.getCustomField('Custom 1', 'classifications.information.custom1');
  custom2Field = this.customFieldsService.getCustomField('Custom 2', 'classifications.information.custom2');
  custom3Field = this.customFieldsService.getCustomField('Custom 3', 'classifications.information.custom3');
  custom4Field = this.customFieldsService.getCustomField('Custom 4', 'classifications.information.custom4');
  custom5Field = this.customFieldsService.getCustomField('Custom 5', 'classifications.information.custom5');
  custom6Field = this.customFieldsService.getCustomField('Custom 6', 'classifications.information.custom6');
  custom7Field = this.customFieldsService.getCustomField('Custom 7', 'classifications.information.custom7');
  custom8Field = this.customFieldsService.getCustomField('Custom 8', 'classifications.information.custom8');

  private action: any;
  private classificationServiceSubscription: Subscription;
  private classificationSelectedSubscription: Subscription;
  private routeSubscription: Subscription;
  private masterAndDetailSubscription: Subscription;
  private classificationSavingSubscription: Subscription;
  private classificationRemoveSubscription: Subscription;
  private selectedClassificationSubscription: Subscription;
  private selectedClassificationTypeSubscription: Subscription;
  private categoriesSubscription: Subscription;
  private domainSubscription: Subscription;

  @ViewChild('ClassificationForm') classificationForm: NgForm;
  toogleValidationMap = new Map<string, boolean>();

  constructor(private classificationsService: ClassificationsService,
    private route: ActivatedRoute,
    private router: Router,
    private masterAndDetailService: MasterAndDetailService,
    private errorModalService: ErrorModalService,
    private requestInProgressService: RequestInProgressService,
    private alertService: AlertService,
    private treeService: TreeService,
    private categoryService: ClassificationCategoriesService,
    private domainService: DomainService,
    private customFieldsService: CustomFieldsService,
    private removeConfirmationService: RemoveConfirmationService,
    private formsValidatorService: FormsValidatorService) {
  }

  ngOnInit() {
    this.categoryService.getClassificationTypes().subscribe((classificationTypes: Array<string>) => {
      this.classificationTypes = classificationTypes;
    });
    this.classificationSelectedSubscription = this.classificationsService.getSelectedClassification()
      .subscribe(classificationSelected => {
        if (this.classification && this.classification.classificationId === classificationSelected.classificationId) { return; }
        this.initProperties();
        if (classificationSelected) {
          this.fillClassificationInformation(classificationSelected);
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
        this.fillClassificationInformation(this.selectedClassification ? this.selectedClassification : new ClassificationDefinition())
      }

      if (!this.classification || this.classification.classificationId !== id && id && id !== '') {
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
    this.removeConfirmationService.setRemoveConfirmation(this.removeClassificationConfirmation.bind(this),
      `You are going to delete classification: ${this.classification.key}. Can you confirm this action?`);
  }

  isFieldValid(field: string): boolean {
    return this.formsValidatorService.isFieldValid(this.classificationForm, field);
  }

  onSubmit() {
    this.formsValidatorService.formSubmitAttempt = true;
    this.formsValidatorService.validateFormInformation(this.classificationForm, this.toogleValidationMap).then(value => {
      if (value) {
        this.onSave();
      }
    });
  }

  private initProperties() {
    this.classification = undefined;
    this.action = undefined
  }

  private onSave() {
    this.requestInProgressService.setRequestInProgress(true);
    if (this.action === ACTION.CREATE) {
      this.classificationSavingSubscription = this.classificationsService.postClassification(this.classification)
        .subscribe((classification: ClassificationDefinition) => {
          this.classification = classification;
          this.classificationsService.selectClassification(classification);
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
          this.cloneClassification(classification);
        }, error => {
          this.errorModalService.triggerError(new ErrorModel('There was error while saving your classification', error))
          this.afterRequest();
        })
    }
  }

  onClear() {
    this.formsValidatorService.formSubmitAttempt = false;
    this.alertService.triggerAlert(new AlertModel(AlertType.INFO, 'Reset edited fields'))
    this.classification = { ...this.classificationClone };
  }


  selectCategory(category: string) {
    this.classification.category = category;
  }

  getCategoryIcon(category: string): Pair {
    return this.categoryService.getCategoryIcon(category);
  }

  spinnerRunning(value) { this.spinnerIsRunning = value; }

  private afterRequest() {
    this.requestInProgressService.setRequestInProgress(false);
    this.classificationsService.triggerClassificationSaved();
  }

  private selectClassification(id: string) {
    if (this.classificationIsAlreadySelected()) {
      return true;
    }
    this.requestInProgress = true;
    this.selectedClassificationSubscription = this.classificationsService.getClassification(id).subscribe(classification => {
      this.selectedClassification = classification;
      this.classificationsService.selectClassification(classification);
      this.requestInProgress = false;
    });
  }

  private classificationIsAlreadySelected(): boolean {
    if (this.action === ACTION.CREATE && this.selectedClassification) { return true }
  }

  private fillClassificationInformation(classificationSelected: ClassificationDefinition) {
    if (this.action === ACTION.CREATE) { // CREATE
      this.initClassificationOnCreation(classificationSelected);
    } else {
      this.classification = classificationSelected;
      this.cloneClassification(classificationSelected);
      this.checkDomainAndRedirect();
    }
  }

  private addDateToClassification() {
    const date = TaskanaDate.getDate();
    this.classification.created = date;
    this.classification.modified = date;
  }

  private initClassificationOnCreation(classificationSelected: ClassificationDefinition) {
    this.classification = new ClassificationDefinition();
    this.classification.parentId = classificationSelected.classificationId;
    this.classification.parentKey = classificationSelected.key;
    this.classification.category = classificationSelected.category;
    this.classification.domain = this.domainService.getSelectedDomainValue();
    this.selectedClassificationSubscription = this.categoryService.getSelectedClassificationType().subscribe(type => {
      if (this.classification) { this.classification.type = type; }
    });
    this.addDateToClassification();
    this.cloneClassification(this.classification);
  }

  private checkDomainAndRedirect() {
    this.domainSubscription = this.domainService.getSelectedDomain().subscribe(domain => {
      if (this.classification && this.classification.domain !== domain) {
        this.backClicked();
      }
    });
  }

  private removeClassificationConfirmation() {
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

  private cloneClassification (classification: ClassificationDefinition) {
    this.classificationClone = { ...classification };
  }

  validChanged(): void {
    this.classification.isValidInDomain = !this.classification.isValidInDomain;
  }

  masterDomainSelected(): boolean {
    return this.domainService.getSelectedDomainValue() === '';
  }

  ngOnDestroy(): void {

    if (this.masterAndDetailSubscription) { this.masterAndDetailSubscription.unsubscribe(); }
    if (this.routeSubscription) { this.routeSubscription.unsubscribe(); }
    if (this.classificationSelectedSubscription) { this.classificationSelectedSubscription.unsubscribe(); }
    if (this.classificationServiceSubscription) { this.classificationServiceSubscription.unsubscribe(); }
    if (this.classificationSavingSubscription) { this.classificationSavingSubscription.unsubscribe(); }
    if (this.classificationRemoveSubscription) { this.classificationRemoveSubscription.unsubscribe(); }
    if (this.selectedClassificationSubscription) { this.selectedClassificationSubscription.unsubscribe(); }
    if (this.selectedClassificationTypeSubscription) { this.selectedClassificationTypeSubscription.unsubscribe(); }
    if (this.categoriesSubscription) { this.categoriesSubscription.unsubscribe(); }
    if (this.domainSubscription) { this.domainSubscription.unsubscribe(); }
  }
}
