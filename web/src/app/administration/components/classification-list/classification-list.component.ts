import { Component, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { ActivatedRoute, Router } from '@angular/router';
import { Select } from '@ngxs/store';

import { TaskanaType } from 'app/shared/models/taskana-type';
import { Classification } from 'app/shared/models/classification';
import { TreeNodeModel } from 'app/shared/models/tree-node';

import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';
import { Pair } from 'app/shared/models/pair';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { EngineConfigurationSelectors } from 'app/shared/store/engine-configuration-store/engine-configuration.selectors';
import { ClassificationSelectors } from 'app/shared/store/classification-store/classification.selectors';
import { ClassificationDefinition } from '../../../shared/models/classification-definition';
import { NOTIFICATION_TYPES } from '../../../shared/models/notifications';

import { ClassificationCategoryImages } from '../../../shared/models/customisation';
import { NotificationService } from '../../../shared/services/notifications/notification.service';

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
    private notificationsService: NotificationService
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

    if (key) {
      this.notificationsService.triggerAlert(
        NOTIFICATION_TYPES.SUCCESS_ALERT_5,
        new Map<string, string>([['classificationKey', key]])
      );
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
