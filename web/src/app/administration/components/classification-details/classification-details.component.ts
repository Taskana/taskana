import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Select, Store } from '@ngxs/store';
import { Observable, Subscription, zip } from 'rxjs';

import { ClassificationDefinition, customFieldCount } from 'app/models/classification-definition';
import { ACTION } from 'app/administration/models/action';
import { AlertModel, AlertType } from 'app/models/alert';

import { highlight } from 'app/shared/animations/validation.animation';
import { TaskanaDate } from 'app/shared/util/taskana.date';

import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';
import { MasterAndDetailService } from 'app/services/masterAndDetail/master-and-detail.service';
import { GeneralModalService } from 'app/services/general-modal/general-modal.service';
import { RequestInProgressService } from 'app/services/requestInProgress/request-in-progress.service';
import { AlertService } from 'app/services/alert/alert.service';
import { TreeService } from 'app/services/tree/tree.service';
import { RemoveConfirmationService } from 'app/services/remove-confirmation/remove-confirmation.service';

import { DomainService } from 'app/services/domain/domain.service';
import { Pair } from 'app/models/pair';
import { NgForm } from '@angular/forms';
import { FormsValidatorService } from 'app/shared/services/forms/forms-validator.service';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { map, take } from 'rxjs/operators';
import { EngineConfigurationSelectors } from 'app/store/engine-configuration-store/engine-configuration.selectors';
import { ClassificationSelectors } from 'app/store/classification-store/classification.selectors';
import { ERROR_TYPES } from '../../../models/errors';
import { ErrorsService } from '../../../services/errors/errors.service';
import { ClassificationCategoryImages, CustomField, getCustomFields } from '../../../models/customisation';

@Component({
  selector: 'taskana-classification-details',
  templateUrl: './classification-details.component.html',
  animations: [highlight],
  styleUrls: ['./classification-details.component.scss']
})
export class ClassificationDetailsComponent implements OnInit, OnDestroy {
  classification: ClassificationDefinition;
  classificationClone: ClassificationDefinition;
  classificationCategories: string[];
  showDetail = false;
  badgeMessage = '';
  requestInProgress = false;
  @Select(ClassificationSelectors.selectCategories) categories$: Observable<string[]>;
  @Select(EngineConfigurationSelectors.selectCategoryIcons) categoryIcons$: Observable<ClassificationCategoryImages>;
  @Select(ClassificationSelectors.selectedClassificationType) selectedClassificationType$: Observable<string>;
  @Select(ClassificationSelectors.selectClassificationTypesObject) classificationTypes$: Observable<Object>;

  spinnerIsRunning = false;
  customFields$: Observable<CustomField[]>;

  @ViewChild('ClassificationForm', { static: false }) classificationForm: NgForm;
  toogleValidationMap = new Map<string, boolean>();
  private action: any;
  private classificationServiceSubscription: Subscription;
  private classificationSelectedSubscription: Subscription;
  private routeSubscription: Subscription;
  private masterAndDetailSubscription: Subscription;
  private classificationSavingSubscription: Subscription;
  private classificationRemoveSubscription: Subscription;
  private domainSubscription: Subscription;
  private importingExportingSubscription: Subscription;

  constructor(private classificationsService: ClassificationsService,
    private route: ActivatedRoute,
    private router: Router,
    private masterAndDetailService: MasterAndDetailService,
    private generalModalService: GeneralModalService,
    private requestInProgressService: RequestInProgressService,
    private alertService: AlertService,
    private treeService: TreeService,
    private domainService: DomainService,
    private removeConfirmationService: RemoveConfirmationService,
    private formsValidatorService: FormsValidatorService,
    private errorsService: ErrorsService,
    private importExportService: ImportExportService,
    private store: Store) {
  }

  ngOnInit() {
    this.customFields$ = this.store.select(EngineConfigurationSelectors.classificationsCustomisation).pipe(
      map(customisation => customisation.information),
      getCustomFields(customFieldCount)
    );
    this.classificationSelectedSubscription = this.classificationsService.getSelectedClassification()
      .subscribe(classificationSelected => {
        if (classificationSelected && this.classification
            && this.classification.classificationId === classificationSelected.classificationId) { return; }
        this.initProperties();
        if (classificationSelected) {
          this.fillClassificationInformation(classificationSelected);
        }
      });

    this.routeSubscription = this.route.params.subscribe(params => {
      let { id } = params;
      delete this.action;
      if (id && id.indexOf('new-classification') !== -1) {
        this.action = ACTION.CREATE;
        this.badgeMessage = 'Creating new classification';
        id = id.replace('new-classification/', '');
        if (id === 'undefined') {
          id = '';
        }
        this.fillClassificationInformation(this.classification ? this.classification : new ClassificationDefinition());
      }

      if (!this.classification || (this.classification.classificationId !== id && id)) {
        this.selectClassification(id);
      }
    });

    this.masterAndDetailSubscription = this.masterAndDetailService.getShowDetail().subscribe(showDetail => {
      this.showDetail = showDetail;
    });

    this.importingExportingSubscription = this.importExportService.getImportingFinished().subscribe((value: Boolean) => {
      if (this.classification.classificationId) {
        this.selectClassification(this.classification.classificationId);
      }
    });
  }

  backClicked(): void {
    this.classificationsService.selectClassification();
    this.router.navigate(['./'], { relativeTo: this.route.parent });
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

  onClear() {
    this.formsValidatorService.formSubmitAttempt = false;
    // new Key: ALERT_TYPES.INFO_ALERT
    this.alertService.triggerAlert(new AlertModel(AlertType.INFO, 'Reset edited fields'));
    this.classification = { ...this.classificationClone };
  }

  selectCategory(category: string) {
    this.classification.category = category;
  }

  getCategoryIcon(category: string): Observable<Pair> {
    return this.categoryIcons$.pipe(map(
      iconMap => (iconMap[category]
        ? new Pair(iconMap[category], category)
        : new Pair(iconMap.missing, 'Category does not match with the configuration'))
    ));
  }

  spinnerRunning(value) {
    this.spinnerIsRunning = value;
  }

  validChanged(): void {
    this.classification.isValidInDomain = !this.classification.isValidInDomain;
  }

  masterDomainSelected(): boolean {
    return this.domainService.getSelectedDomainValue() === '';
  }

  private initProperties() {
    delete this.classification;
    delete this.action;
  }

  private async onSave() {
    this.requestInProgressService.setRequestInProgress(true);
    if (this.action === ACTION.CREATE) {
      this.classificationSavingSubscription = this.classificationsService.postClassification(this.classification)
        .subscribe((classification: ClassificationDefinition) => {
          this.classification = classification;
          this.classificationsService.selectClassification(classification);
          // new Key ALERT_TYPES.SUCCESS_ALERT_2
          this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, `Classification ${classification.key} was saved successfully`));
          this.afterRequest();
        },
        error => {
          this.errorsService.updateError(ERROR_TYPES.CREATE_ERR, error);
          this.afterRequest();
        });
    } else {
      try {
        this.classification = (<ClassificationDefinition> await this.classificationsService.putClassification(
          this.classification._links.self.href, this.classification
        ));
        this.afterRequest();
        // new Key: ALERT_TYPES.SUCCESS_ALERT_3
        this.alertService.triggerAlert(
          new AlertModel(AlertType.SUCCESS, `Classification ${this.classification.key} was saved successfully`)
        );
        this.cloneClassification(this.classification);
      } catch (error) {
        this.errorsService.updateError(ERROR_TYPES.SAVE_ERR, error);
        this.afterRequest();
      }
    }
  }

  private afterRequest() {
    this.requestInProgressService.setRequestInProgress(false);
    this.classificationsService.triggerClassificationSaved();
  }

  private async selectClassification(id: string) {
    if (!this.classificationIsAlreadySelected()) {
      this.requestInProgress = true;
      const classification = await this.classificationsService.getClassification(id);
      this.fillClassificationInformation(classification);
      this.classificationsService.selectClassification(classification);
      this.requestInProgress = false;
    }
  }

  private classificationIsAlreadySelected(): boolean {
    return this.action === ACTION.CREATE && !!this.classification;
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

  private addDateToClassification(classification: ClassificationDefinition) {
    const date = TaskanaDate.getDate();
    classification.created = date;
    classification.modified = date;
  }

  private initClassificationOnCreation(classificationSelected: ClassificationDefinition) {
    zip(this.categories$, this.selectedClassificationType$).pipe(take(1)).subscribe(([categories, selectedType]: [string[], string]) => {
      const tempClassification: ClassificationDefinition = new ClassificationDefinition();
      // tempClassification.parentId = classificationSelected.classificationId;
      // tempClassification.parentKey = classificationSelected.key;
      [tempClassification.category] = categories;
      tempClassification.domain = this.domainService.getSelectedDomainValue();
      tempClassification.type = selectedType;
      this.addDateToClassification(tempClassification);
      this.classification = tempClassification;
      this.cloneClassification(this.classification);
    });
  }

  private checkDomainAndRedirect() {
    this.domainSubscription = this.domainService.getSelectedDomain().subscribe(domain => {
      if (domain !== '' && this.classification && this.classification.domain !== domain) {
        this.backClicked();
      }
    });
  }

  private removeClassificationConfirmation() {
    if (!this.classification || !this.classification.classificationId) {
      this.errorsService.updateError(ERROR_TYPES.SELECT_ERR);
      return;
    }
    this.requestInProgressService.setRequestInProgress(true);
    this.treeService.setRemovedNodeId(this.classification.classificationId);

    this.classificationRemoveSubscription = this.classificationsService
      .deleteClassification(this.classification._links.self.href)
      .subscribe(() => {
        const { key } = this.classification;
        delete this.classification;
        this.afterRequest();
        this.classificationsService.selectClassification();
        this.router.navigate(['taskana/administration/classifications']);
        // new Key: ALERT_TYPES.SUCCESS_ALERT_4
        this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, `Classification ${key} was removed successfully`));
      }, error => {
        this.errorsService.updateError(ERROR_TYPES.REMOVE_ERR, error);
        this.afterRequest();
      });
  }

  private cloneClassification(classification: ClassificationDefinition) {
    this.classificationClone = { ...classification };
  }

  getClassificationCustom(customNumber: number): string {
    return `custom${customNumber}`;
  }

  // TODO: Remove when classification is in store
  getAvailableCategories(type: string) {
    let returnCategories: string[] = [];
    this.classificationTypes$.subscribe(classTypes => {
      returnCategories = classTypes[type];
    });

    return returnCategories;
  }

  ngOnDestroy(): void {
    if (this.masterAndDetailSubscription) {
      this.masterAndDetailSubscription.unsubscribe();
    }
    if (this.routeSubscription) {
      this.routeSubscription.unsubscribe();
    }
    if (this.classificationSelectedSubscription) {
      this.classificationSelectedSubscription.unsubscribe();
    }
    if (this.classificationServiceSubscription) {
      this.classificationServiceSubscription.unsubscribe();
    }
    if (this.classificationSavingSubscription) {
      this.classificationSavingSubscription.unsubscribe();
    }
    if (this.classificationRemoveSubscription) {
      this.classificationRemoveSubscription.unsubscribe();
    }
    if (this.domainSubscription) {
      this.domainSubscription.unsubscribe();
    }
    if (this.importingExportingSubscription) {
      this.importingExportingSubscription.unsubscribe();
    }
  }
}
