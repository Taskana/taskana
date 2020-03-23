import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription, Observable } from 'rxjs';
import { Router, ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';

import { TaskanaType } from 'app/models/taskana-type';
import { Classification } from 'app/models/classification';
import { TreeNodeModel } from 'app/models/tree-node';

import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';
import { ClassificationCategoriesService } from 'app/shared/services/classifications/classification-categories.service';
import { Pair } from 'app/models/pair';
import { ImportExportService } from 'app/administration/services/import-export/import-export.service';
import { ClassificationDefinition } from '../../../../models/classification-definition';
import { AlertModel, AlertType } from '../../../../models/alert';
import { AlertService } from '../../../../services/alert/alert.service';
import { ERROR_TYPES } from '../../../../models/errors';

import { ClassificationStoreSelectors, AdministrationState } from '../../../administration-store';

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
  classificationTypes$: Observable<string[]>;
  classificationTypeSelected$: Observable<string>;
  categories$: Observable<string[]>;
  classificationServiceSubscription: Subscription;
  classificationTypeSubscription: Subscription;
  classificationSelectedSubscription: Subscription;
  classificationSavedSubscription: Subscription;
  importingExportingSubscription: Subscription;

  constructor(
    private classificationService: ClassificationsService,
    private router: Router,
    private route: ActivatedRoute,
    private categoryService: ClassificationCategoriesService,
    private importExportService: ImportExportService,
    private alertService: AlertService,
    private store$: Store<AdministrationState.State>,
  ) {
  }

  ngOnInit() {
    this.classificationTypes$ = this.store$.select(ClassificationStoreSelectors.selectClassificationTypes);
    this.classificationTypeSelected$ = this.store$.select(ClassificationStoreSelectors.selectSelectedClassificationType);
    this.categories$ = this.store$.select(ClassificationStoreSelectors.selectCategories);

    this.classificationSavedSubscription = this.classificationService
      .classificationSavedTriggered()
      .subscribe(() => {
        this.performRequest(true);
      });

    // this should only be temporary until more actions are implemented
    this.classificationTypeSubscription = this.classificationTypeSelected$.subscribe(type => {
      this.categoryService.selectClassificationType(type);
      this.performRequest();
    });

    this.importingExportingSubscription = this.importExportService.getImportingFinished().subscribe((value: Boolean) => {
      this.performRequest(true);
    });
  }

  selectClassificationType(classificationTypeSelected: string) {
    this.classifications = [];
    this.categoryService.selectClassificationType(classificationTypeSelected);
    this.getClassifications();
    this.selectClassification();
  }

  selectClassification(id?: string) {
    this.selectedId = id;
    if (!id) {
      this.router.navigate(['taskana/administration/classifications']);
      return;
    }
    this.router.navigate([{ outlets: { detail: [this.selectedId] } }], { relativeTo: this.route });
  }

  addClassification() {
    this.router.navigate([{ outlets: { detail: [`new-classification/${this.selectedId}`] } }], { relativeTo: this.route });
  }

  selectCategory(category: string) {
    this.selectedCategory = category;
  }

  getCategoryIcon(category: string): Pair {
    return this.categoryService.getCategoryIcon(category);
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
