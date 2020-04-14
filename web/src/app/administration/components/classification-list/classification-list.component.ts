import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Router, ActivatedRoute } from '@angular/router';
import { Select } from '@ngxs/store';

import { TaskanaType } from 'app/models/taskana-type';
import { Classification } from 'app/models/classification';
import { TreeNodeModel } from 'app/models/tree-node';

import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';
import { Pair } from 'app/models/pair';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { EngineConfigurationSelectors } from 'app/store/engine-configuration-store/engine-configuration.selectors';
import { ClassificationSelectors } from 'app/store/classification-store/classification.selectors';
import { ClassificationDefinition } from '../../../models/classification-definition';
import { AlertModel, AlertType } from '../../../models/alert';
import { AlertService } from '../../../services/alert/alert.service';
import { ERROR_TYPES } from '../../../models/errors';

import { ClassificationCategoryImages } from '../../../models/customisation';

@Component({
  selector: 'taskana-classification-list',
  templateUrl: './classification-list.component.html',
  styleUrls: ['./classification-list.component.scss']
})
export class ClassificationListComponent implements OnInit, OnDestroy {
  selectedCategory = '';
  selectedId: string;
  selectionToImport = TaskanaType.CLASSIFICATIONS;
  requestInProgress = false;
  initialized = false;
  inputValue: string;
  classifications: Classification[] = [];
  @Select(ClassificationSelectors.classificationTypes) classificationTypes$: Observable<string[]>;
  @Select(ClassificationSelectors.selectedClassificationType) classificationTypeSelected$: Observable<string>;
  @Select(ClassificationSelectors.selectCategories) categories$: Observable<string[]>;
  @Select(EngineConfigurationSelectors.selectCategoryIcons) categoryIcons$: Observable<ClassificationCategoryImages>;
  classificationServiceSubscription: Subscription;
  classificationTypeSubscription: Subscription;
  classificationSelectedSubscription: Subscription;
  classificationSavedSubscription: Subscription;
  importingExportingSubscription: Subscription;

  constructor(
    private classificationService: ClassificationsService,
    private router: Router,
    private route: ActivatedRoute,
    private importExportService: ImportExportService,
    private alertService: AlertService
  ) {
  }

  ngOnInit() {
    this.classificationSavedSubscription = this.classificationService
      .classificationSavedTriggered()
      .subscribe(() => {
        this.performRequest(true);
      });

    this.classificationTypeSubscription = this.classificationTypeSelected$.subscribe(() => {
      this.performRequest();
      this.selectClassification();
      this.selectedCategory = '';
    });

    this.importingExportingSubscription = this.importExportService.getImportingFinished().subscribe(() => {
      this.performRequest(true);
    });
  }

  selectClassification(id?: string) {
    this.selectedId = id;

    if (id) {
      this.router.navigate([{ outlets: { detail: [this.selectedId] } }], { relativeTo: this.route });
    }
  }

  addClassification() {
    this.router.navigate([{ outlets: { detail: [`new-classification/${this.selectedId}`] } }], { relativeTo: this.route });
  }

  selectCategory(category: string) {
    this.selectedCategory = category;
  }

  getCategoryIcon(category: string): Observable<Pair> {
    return this.categoryIcons$.pipe(
      map(
        iconMap => (iconMap[category]
          ? new Pair(iconMap[category], category)
          : new Pair(iconMap.missing, 'Category does not match with the configuration'))
      )
    );
  }

  private performRequest(forceRequest = false) {
    if (this.initialized && !forceRequest) {
      return;
    }

    this.requestInProgress = true;
    this.classifications = [];

    if (this.classificationServiceSubscription) { this.classificationServiceSubscription.unsubscribe(); }
    if (this.classificationSelectedSubscription) { this.classificationSelectedSubscription.unsubscribe(); }

    this.classificationServiceSubscription = this.classificationService.getClassifications()
      .subscribe((classifications: TreeNodeModel[]) => {
        this.requestInProgress = false;
        this.classifications = classifications;
      });
    this.classificationSelectedSubscription = this.classificationService.getSelectedClassification()
      .subscribe((classificationSelected: ClassificationDefinition) => {
        this.selectedId = classificationSelected ? classificationSelected.classificationId : undefined;
      });

    this.initialized = true;
  }

  private getClassifications(key?: string) {
    this.requestInProgress = true;
    this.classificationService.getClassifications()
      .subscribe((classifications: TreeNodeModel[]) => {
        this.classifications = classifications;
        this.requestInProgress = false;
      });

    // new Error-Key: ALERT_TYPES.SUCCESS_ALERT_5
    if (key) {
      this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, `Classification ${key} was saved successfully`));
    }
  }

  private switchTaskanaSpinner($event) {
    this.requestInProgress = $event;
  }

  ngOnDestroy(): void {
    if (this.classificationServiceSubscription) { this.classificationServiceSubscription.unsubscribe(); }
    if (this.classificationSelectedSubscription) { this.classificationSelectedSubscription.unsubscribe(); }
    if (this.classificationSavedSubscription) { this.classificationSavedSubscription.unsubscribe(); }
    if (this.importingExportingSubscription) { this.importingExportingSubscription.unsubscribe(); }
    if (this.classificationTypeSubscription) { this.classificationTypeSubscription.unsubscribe(); }
  }
}
