import { Component, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import { Actions, ofActionCompleted, ofActionDispatched, Select, Store } from '@ngxs/store';

import { ImportExportService } from 'app/administration/services/import-export.service';

import { TaskanaType } from 'app/shared/models/taskana-type';
import { EngineConfigurationSelectors } from 'app/shared/store/engine-configuration-store/engine-configuration.selectors';
import { ClassificationSelectors } from 'app/shared/store/classification-store/classification.selectors';
import { Location } from '@angular/common';
import { ClassificationCategoryImages } from '../../../shared/models/customisation';

import {
  GetClassifications,
  CreateClassification
} from '../../../shared/store/classification-store/classification.actions';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { ClassificationSummary } from '../../../shared/models/classification-summary';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { Pair } from '../../../shared/models/pair';

@Component({
  selector: 'taskana-administration-classification-list',
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
  @Select(ClassificationSelectors.classifications) classifications$: Observable<ClassificationSummary[]>;
  @Select(EngineConfigurationSelectors.selectCategoryIcons) categoryIcons$: Observable<ClassificationCategoryImages>;

  destroy$ = new Subject<void>();
  classifications: ClassificationSummary[];

  constructor(
    private location: Location,
    private importExportService: ImportExportService,
    private domainService: DomainService,
    private requestInProgressService: RequestInProgressService,
    private store: Store,
    private ngxsActions$: Actions
  ) {
    this.ngxsActions$.pipe(ofActionDispatched(GetClassifications), takeUntil(this.destroy$)).subscribe(() => {
      this.requestInProgressService.setRequestInProgress(true);
    });
    this.ngxsActions$.pipe(ofActionCompleted(GetClassifications), takeUntil(this.destroy$)).subscribe(() => {
      this.requestInProgressService.setRequestInProgress(false);
    });
  }

  ngOnInit() {
    this.classifications$.pipe(takeUntil(this.destroy$)).subscribe((classifications) => {
      this.classifications = classifications;
    });

    this.classificationTypeSelected$.pipe(takeUntil(this.destroy$)).subscribe(() => {
      this.store.dispatch(new GetClassifications());
      this.selectedCategory = '';
    });

    this.importExportService
      .getImportingFinished()
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.store.dispatch(new GetClassifications());
      });

    // needed, so that the list updates, when domain gets changed (could be placed anywhere and should be removed, when domain is in store)
    this.domainService
      .getSelectedDomain()
      .pipe(takeUntil(this.destroy$))
      .subscribe((domain) => {
        this.store.dispatch(GetClassifications);
      });

    this.requestInProgressService
      .getRequestInProgress()
      .pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        this.requestInProgress = value;
      });
  }

  addClassification() {
    this.store.dispatch(new CreateClassification());
    this.location.go(this.location.path().replace(/(classifications).*/g, 'classifications/new-classification'));
  }

  getCategoryIcon(category: string): Observable<Pair<string, string>> {
    return this.categoryIcons$.pipe(
      map((iconMap) => {
        if (category === '') {
          return { left: iconMap['all'], right: 'All' };
        }
        return iconMap[category]
          ? { left: iconMap[category], right: category }
          : { left: iconMap.missing, right: 'Category does not match with the configuration' };
      })
    );
  }

  selectCategory(category: string) {
    this.selectedCategory = category;
  }

  setRequestInProgress(value: boolean) {
    this.requestInProgressService.setRequestInProgress(value);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
