import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Select, Store } from '@ngxs/store';
import { Observable, Subject, zip } from 'rxjs';

import { ClassificationDefinition,
  customFieldCount } from 'app/shared/models/classification-definition';
import { ACTION } from 'app/shared/models/action';

import { highlight } from 'theme/animations/validation.animation';
import { TaskanaDate } from 'app/shared/util/taskana.date';

import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';
import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';

import { DomainService } from 'app/shared/services/domain/domain.service';
import { Pair } from 'app/shared/models/pair';
import { NgForm } from '@angular/forms';
import { FormsValidatorService } from 'app/shared/services/forms-validator/forms-validator.service';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { map, take, takeUntil } from 'rxjs/operators';
import { EngineConfigurationSelectors } from 'app/shared/store/engine-configuration-store/engine-configuration.selectors';
import { ClassificationSelectors } from 'app/shared/store/classification-store/classification.selectors';
import { Location } from '@angular/common';
import { NOTIFICATION_TYPES } from '../../../shared/models/notifications';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { ClassificationCategoryImages,
  CustomField,
  getCustomFields } from '../../../shared/models/customisation';

import { CreateClassification,
  RemoveSelectedClassification,
  RestoreSelectedClassification,
  SaveClassification } from '../../../shared/store/classification-store/classification.actions';

@Component({
  selector: 'taskana-classification-details',
  templateUrl: './classification-details.component.html',
  animations: [highlight],
  styleUrls: ['./classification-details.component.scss']
})
export class ClassificationDetailsComponent implements OnInit, OnDestroy {
  classification: ClassificationDefinition;
  badgeMessage = '';
  requestInProgress = false;
  @Select(ClassificationSelectors.selectCategories) categories$: Observable<string[]>;
  @Select(EngineConfigurationSelectors.selectCategoryIcons) categoryIcons$: Observable<ClassificationCategoryImages>;
  @Select(ClassificationSelectors.selectedClassificationType) selectedClassificationType$: Observable<string>;
  @Select(ClassificationSelectors.selectClassificationTypesObject) classificationTypes$: Observable<Object>;
  @Select(ClassificationSelectors.selectedClassification) selectedClassification$: Observable<ClassificationDefinition>;
  @Select(ClassificationSelectors.activeAction) action$: Observable<ACTION>;

  spinnerIsRunning = false;
  customFields$: Observable<CustomField[]>;

  @ViewChild('ClassificationForm', { static: false }) classificationForm: NgForm;
  toogleValidationMap = new Map<string, boolean>();
  action: ACTION;
  destroy$ = new Subject<void>();

  constructor(
    private classificationsService: ClassificationsService,
    private location: Location,
    private requestInProgressService: RequestInProgressService,
    private domainService: DomainService,
    private formsValidatorService: FormsValidatorService,
    private notificationsService: NotificationService,
    private importExportService: ImportExportService,
    private store: Store
  ) {
  }

  ngOnInit() {
    this.customFields$ = this.store.select(EngineConfigurationSelectors.classificationsCustomisation).pipe(
      map(customisation => customisation.information),
      getCustomFields(customFieldCount)
    );
    this.selectedClassification$.pipe(takeUntil(this.destroy$)).subscribe(data => {
      this.fillClassificationInformation(data);
    });
    this.action$.pipe(takeUntil(this.destroy$)).subscribe(data => {
      this.action = data;
      if (this.action === ACTION.CREATE) {
        this.initClassificationOnCreation();
        this.badgeMessage = 'Creating new classification';
      } else {
        this.badgeMessage = '';
      }
    });

    this.importExportService.getImportingFinished().pipe(takeUntil(this.destroy$)).subscribe(() => {
      if (this.classification.classificationId) {
        this.selectClassification(this.classification.classificationId);
      }
    });
  }


  backClicked(): void {
    this.location.go(this.location.path().replace(/(classifications).*/g, 'classifications'));
  }

  removeClassification() {
    this.notificationsService.showDialog(`You are going to delete classification: ${this.classification.key}. Can you confirm this action?`,
      this.removeClassificationConfirmation.bind(this));
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

  onRestore() {
    this.formsValidatorService.formSubmitAttempt = false;
    if (this.action === ACTION.CREATE) {
      this.classification = new ClassificationDefinition();
      this.notificationsService.showToast(NOTIFICATION_TYPES.INFO_ALERT);
    } else {
      this.store.dispatch(
        new RestoreSelectedClassification(this.classification.classificationId)
      ).pipe(take(1)).subscribe(ctx => {
        this.notificationsService.showToast(NOTIFICATION_TYPES.INFO_ALERT);
      });
    }
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

  private async onSave() {
    this.requestInProgressService.setRequestInProgress(true);
    if (this.action === ACTION.CREATE) {
      this.store.dispatch(
        new CreateClassification(this.classification)
      ).pipe(take(1)).subscribe(store => {
        this.notificationsService.showToast(
          NOTIFICATION_TYPES.SUCCESS_ALERT_2,
          new Map<string, string>([['classificationKey', store.classification.selectedClassification.key]])
        );
        this.afterRequest();
      }, error => {
        this.notificationsService.triggerError(NOTIFICATION_TYPES.CREATE_ERR, error);
        this.afterRequest();
      });
    } else {
      try {
        this.store.dispatch(new SaveClassification(this.classification));
        this.afterRequest();
        this.notificationsService.showToast(
          NOTIFICATION_TYPES.SUCCESS_ALERT_3,
          new Map<string, string>([['classificationKey', this.classification.key]])
        );
      } catch (error) {
        this.notificationsService.triggerError(NOTIFICATION_TYPES.SAVE_ERR, error);
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
      const classification = await this.classificationsService.getClassification(id).toPromise();
      this.fillClassificationInformation(classification);
      this.requestInProgress = false;
    }
  }

  private classificationIsAlreadySelected(): boolean {
    return this.action === ACTION.CREATE && !!this.classification;
  }

  private fillClassificationInformation(classificationSelected: ClassificationDefinition) {
    this.classification = { ...classificationSelected };
  }

  private addDateToClassification(classification: ClassificationDefinition) {
    const date = TaskanaDate.getDate();
    classification.created = date;
    classification.modified = date;
  }

  private initClassificationOnCreation() {
    zip(this.categories$, this.selectedClassificationType$).pipe(take(1)).subscribe(([categories, selectedType]: [string[], string]) => {
      const tempClassification: ClassificationDefinition = new ClassificationDefinition();
      [tempClassification.category] = categories;
      tempClassification.domain = this.domainService.getSelectedDomainValue();
      tempClassification.type = selectedType;
      this.addDateToClassification(tempClassification);
      this.classification = tempClassification;
    });
  }

  private removeClassificationConfirmation() {
    if (!this.classification || !this.classification.classificationId) {
      this.notificationsService.triggerError(NOTIFICATION_TYPES.SELECT_ERR);
      return;
    }
    this.requestInProgressService.setRequestInProgress(true);

    this.store.dispatch(new RemoveSelectedClassification()).pipe(take(1)).subscribe(() => {
      this.notificationsService.showToast(NOTIFICATION_TYPES.SUCCESS_ALERT_4,
        new Map<string, string>([['classificationKey', this.classification.key]]));
      this.afterRequest();
    });
  }

  getClassificationCustom(customNumber: number): string {
    return `custom${customNumber}`;
  }

  getAvailableCategories(type: string) {
    let returnCategories: string[] = [];
    this.classificationTypes$.pipe(take(1)).subscribe(classTypes => {
      returnCategories = classTypes[type];
    });

    return returnCategories;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
