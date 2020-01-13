import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { Router, ActivatedRoute } from '@angular/router';

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
  categories: Array<string> = [];
  classifications: Array<Classification> = [];
  classificationsTypes: Array<string> = [];
  classificationTypeSelected: string;
  classificationServiceSubscription: Subscription;
  classificationTypeServiceSubscription: Subscription;
  classificationSelectedSubscription: Subscription;
  classificationSavedSubscription: Subscription;
  selectedClassificationSubscription: Subscription;
  categoriesSubscription: Subscription;
  importingExportingSubscription: Subscription;

  constructor(
    private classificationService: ClassificationsService,
    private router: Router,
    private route: ActivatedRoute,
    private categoryService: ClassificationCategoriesService,
    private importExportService: ImportExportService,
    private alertService: AlertService
) {
  }

  ngOnInit() {
    this.classificationSavedSubscription = this.classificationService
      .classificationSavedTriggered()
      .subscribe(value => {
        this.performRequest(true);
      });
    this.selectedClassificationSubscription = this.categoryService.getSelectedClassificationType().subscribe(value => {
      this.classificationTypeSelected = value;
      this.performRequest();
    });

    this.categoriesSubscription = this.categoryService.getCategories(this.classificationTypeSelected).subscribe((categories: Array<string>) => {
        this.categories = categories;
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
      .subscribe((classifications: Array<TreeNodeModel>) => {
        this.requestInProgress = false;
        this.classifications = classifications;
        this.classificationTypeServiceSubscription = this.categoryService.getClassificationTypes()
          .subscribe((classificationsTypes: Array<string>) => {
            this.classificationsTypes = classificationsTypes;
          });
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
      .subscribe((classifications: Array<TreeNodeModel>) => {
        this.classifications = classifications;
        this.requestInProgress = false;
      });

    if (key) {
      this.alertService.triggerAlert(new AlertModel(AlertType.SUCCESS, `Classification ${key} was saved successfully`));
    }
  }

  private switchTaskanaSpinner($event) {
    this.requestInProgress = $event;
  }

  ngOnDestroy(): void {
    if (this.classificationServiceSubscription) { this.classificationServiceSubscription.unsubscribe(); }
    if (this.classificationTypeServiceSubscription) { this.classificationTypeServiceSubscription.unsubscribe(); }
    if (this.classificationSelectedSubscription) { this.classificationSelectedSubscription.unsubscribe(); }
    if (this.classificationSavedSubscription) { this.classificationSavedSubscription.unsubscribe(); }
    if (this.importingExportingSubscription) { this.importingExportingSubscription.unsubscribe(); }
  }
}
