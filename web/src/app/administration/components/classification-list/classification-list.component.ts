import { Component, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import { Actions, ofActionCompleted, ofActionDispatched, Select, Store } from '@ngxs/store';

import { ImportExportService } from 'app/administration/services/import-export.service';

import { TaskanaType } from 'app/shared/models/taskana-type';
import { Pair } from 'app/shared/models/pair';
import { ClassificationsService } from 'app/shared/services/classifications/classifications.service';
import { EngineConfigurationSelectors } from 'app/shared/store/engine-configuration-store/engine-configuration.selectors';
import { ClassificationSelectors } from 'app/shared/store/classification-store/classification.selectors';
import { Location } from '@angular/common';
import { ClassificationCategoryImages } from '../../../shared/models/customisation';

import { GetClassifications, SetActiveAction } from '../../../shared/store/classification-store/classification.actions';
import { ACTION } from '../../../shared/models/action';
import { TreeNodeModel } from '../../../shared/models/tree-node';


@Component({
  selector: 'taskana-classification-list',
  templateUrl: './classification-list.component.html',
  styleUrls: ['./classification-list.component.scss']
})
export class ClassificationListComponent implements OnInit, OnDestroy {
  taskanaType = TaskanaType;
  requestInProgress = true;
  inputValue: string;
  selectedCategory = '';

  @Select(ClassificationSelectors.classificationTypes) classificationTypes$: Observable<string[]>;
  @Select(ClassificationSelectors.selectedClassificationType) classificationTypeSelected$: Observable<string>;
  @Select(ClassificationSelectors.selectCategories) categories$: Observable<string[]>;
  @Select(ClassificationSelectors.classifications) classifications$: Observable<TreeNodeModel[]>;
  @Select(ClassificationSelectors.activeAction) activeAction$: Observable<ACTION>;
  @Select(EngineConfigurationSelectors.selectCategoryIcons) categoryIcons$: Observable<ClassificationCategoryImages>;

  action: ACTION;
  destroy$ = new Subject<void>();
  classifications: TreeNodeModel[];

  constructor(
    private classificationService: ClassificationsService,
    private location: Location,
    private importExportService: ImportExportService,
    private store: Store,
    private ngxsActions$: Actions
  ) {
    this.ngxsActions$.pipe(ofActionDispatched(GetClassifications),
      takeUntil(this.destroy$))
      .subscribe(() => {
        this.requestInProgress = true;
      });
    this.ngxsActions$.pipe(ofActionCompleted(GetClassifications),
      takeUntil(this.destroy$))
      .subscribe(() => {
        this.requestInProgress = false;
      });
  }

  ngOnInit() {
    this.classifications$.pipe(takeUntil(this.destroy$)).subscribe(classifications => {
      if (classifications) {
        this.classifications = [...classifications];
      }
    });

    this.classificationService
      .classificationSavedTriggered()
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.store.dispatch(new GetClassifications());
      });

    this.classificationTypeSelected$
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.store.dispatch(new GetClassifications());
        this.selectedCategory = '';
      });

    this.importExportService.getImportingFinished()
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.store.dispatch(new GetClassifications());
      });

    this.activeAction$
      .pipe(takeUntil(this.destroy$))
      .subscribe(action => {
        this.action = action;
      });
  }

  addClassification() {
    if (this.action !== ACTION.CREATE) {
      this.store.dispatch(new SetActiveAction(ACTION.CREATE));
    }
    this.location.go(this.location.path().replace(/(classifications).*/g, 'classifications/new-classification'));
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

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
