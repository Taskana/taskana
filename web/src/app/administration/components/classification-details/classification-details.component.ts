import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Select, Store } from '@ngxs/store';
import { Observable, Subject } from 'rxjs';

import { highlight } from 'app/shared/animations/validation.animation';

import { RequestInProgressService } from 'app/shared/services/request-in-progress/request-in-progress.service';

import { DomainService } from 'app/shared/services/domain/domain.service';
import { NgForm } from '@angular/forms';
import { FormsValidatorService } from 'app/shared/services/forms-validator/forms-validator.service';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { map, take, takeUntil } from 'rxjs/operators';
import { EngineConfigurationSelectors } from 'app/shared/store/engine-configuration-store/engine-configuration.selectors';
import { ClassificationSelectors } from 'app/shared/store/classification-store/classification.selectors';
import { Location } from '@angular/common';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { ClassificationCategoryImages, CustomField, getCustomFields } from '../../../shared/models/customisation';
import { Classification } from '../../../shared/models/classification';
import { customFieldCount } from '../../../shared/models/classification-summary';

import {
  CopyClassification,
  DeselectClassification,
  RemoveSelectedClassification,
  RestoreSelectedClassification,
  SaveCreatedClassification,
  SaveModifiedClassification,
  SelectClassification
} from '../../../shared/store/classification-store/classification.actions';
import { Pair } from '../../../shared/models/pair';
import { trimForm } from '../../../shared/util/form-trimmer';

@Component({
  selector: 'kadai-administration-classification-details',
  templateUrl: './classification-details.component.html',
  animations: [highlight],
  styleUrls: ['./classification-details.component.scss']
})
export class ClassificationDetailsComponent implements OnInit, OnDestroy {
  classification: Classification;
  @Select(ClassificationSelectors.selectCategories) categories$: Observable<string[]>;
  @Select(EngineConfigurationSelectors.selectCategoryIcons) categoryIcons$: Observable<ClassificationCategoryImages>;
  @Select(ClassificationSelectors.selectedClassificationType) selectedClassificationType$: Observable<string>;
  @Select(ClassificationSelectors.selectedClassification) selectedClassification$: Observable<Classification>;
  @Select(ClassificationSelectors.getBadgeMessage) badgeMessage$: Observable<string>;

  customFields$: Observable<CustomField[]>;
  isCreatingNewClassification: boolean = false;
  readonly lengthError = 'You have reached the maximum length for this field';
  inputOverflowMap = new Map<string, boolean>();
  validateInputOverflow: Function;
  requestInProgress: boolean;

  @ViewChild('ClassificationForm') classificationForm: NgForm;
  toggleValidationMap = new Map<string, boolean>();
  destroy$ = new Subject<void>();

  constructor(
    private location: Location,
    private requestInProgressService: RequestInProgressService,
    private domainService: DomainService,
    private formsValidatorService: FormsValidatorService,
    private notificationsService: NotificationService,
    private importExportService: ImportExportService,
    private store: Store
  ) {}

  ngOnInit() {
    this.customFields$ = this.store.select(EngineConfigurationSelectors.classificationsCustomisation).pipe(
      map((customisation) => customisation.information),
      getCustomFields(customFieldCount)
    );

    this.selectedClassification$.pipe(takeUntil(this.destroy$)).subscribe((classification) => {
      this.classification = { ...classification };
      this.isCreatingNewClassification = typeof this.classification.classificationId === 'undefined';
    });

    this.importExportService
      .getImportingFinished()
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.store.dispatch(new SelectClassification(this.classification.classificationId));
      });

    this.requestInProgressService
      .getRequestInProgress()
      .pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        this.requestInProgress = value;
      });

    this.formsValidatorService.inputOverflowObservable.pipe(takeUntil(this.destroy$)).subscribe((inputOverflowMap) => {
      this.inputOverflowMap = inputOverflowMap;
    });
    this.validateInputOverflow = (inputFieldModel, maxLength) => {
      this.formsValidatorService.validateInputOverflow(inputFieldModel, maxLength);
    };
  }

  isFieldValid(field: string): boolean {
    return this.formsValidatorService.isFieldValid(this.classificationForm, field);
  }

  onSubmit() {
    this.formsValidatorService.formSubmitAttempt = true;
    trimForm(this.classificationForm);
    this.formsValidatorService
      .validateFormInformation(this.classificationForm, this.toggleValidationMap)
      .then((value) => {
        if (value) {
          this.onSave();
        }
      });
  }

  onRestore() {
    this.formsValidatorService.formSubmitAttempt = false;
    this.store
      .dispatch(new RestoreSelectedClassification(this.classification.classificationId))
      .pipe(take(1))
      .subscribe(() => {
        this.notificationsService.showSuccess('CLASSIFICATION_RESTORE');
      });
  }

  onCopy() {
    if (this.isCreatingNewClassification) {
      this.notificationsService.showError('CLASSIFICATION_COPY_NOT_CREATED');
    } else {
      this.store.dispatch(new CopyClassification());
    }
  }

  onCloseClassification() {
    this.store.dispatch(new DeselectClassification());
  }

  getCategoryIcon(category: string): Observable<Pair<string, string>> {
    return this.categoryIcons$.pipe(
      map((iconMap) =>
        iconMap[category]
          ? { left: iconMap[category], right: category }
          : { left: iconMap.missing, right: 'Category does not match with the configuration' }
      )
    );
  }

  validChanged(): void {
    this.classification.isValidInDomain = !this.classification.isValidInDomain;
  }

  masterDomainSelected(): boolean {
    return this.domainService.getSelectedDomainValue() === '';
  }

  getClassificationCustom(customNumber: number): string {
    return `custom${customNumber}`;
  }

  async onSave() {
    this.requestInProgressService.setRequestInProgress(true);
    if (typeof this.classification.classificationId === 'undefined') {
      this.store
        .dispatch(new SaveCreatedClassification(this.classification))
        .pipe(take(1))
        .subscribe((store) => {
          this.notificationsService.showSuccess('CLASSIFICATION_CREATE', {
            classificationKey: store.classification.selectedClassification.key
          });
          this.location.go(
            this.location
              .path()
              .replace(
                /(classifications).*/g,
                `classifications/(detail:${store.classification.selectedClassification.classificationId})`
              )
          );
          this.afterRequest();
        });
    } else {
      try {
        this.store
          .dispatch(new SaveModifiedClassification(this.classification))
          .pipe(take(1))
          .subscribe(() => {
            this.afterRequest();
            this.notificationsService.showSuccess('CLASSIFICATION_UPDATE', {
              classificationKey: this.classification.key
            });
          });
      } catch (error) {
        this.afterRequest();
      }
    }
  }

  onRemoveClassification() {
    this.notificationsService.showDialog(
      'CLASSIFICATION_DELETE',
      { classificationKey: this.classification.key },
      this.removeClassificationConfirmation.bind(this)
    );
  }

  removeClassificationConfirmation() {
    this.requestInProgressService.setRequestInProgress(true);

    this.store
      .dispatch(new RemoveSelectedClassification())
      .pipe(take(1))
      .subscribe(() => {
        this.notificationsService.showSuccess('CLASSIFICATION_REMOVE', { classificationKey: this.classification.key });
        this.afterRequest();
      });
    this.location.go(this.location.path().replace(/(classifications).*/g, 'classifications'));
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private afterRequest() {
    this.requestInProgressService.setRequestInProgress(false);
  }
}
