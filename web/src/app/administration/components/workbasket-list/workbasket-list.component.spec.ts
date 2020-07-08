import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { WorkbasketSummary } from 'app/shared/models/workbasket-summary';
import { WorkbasketSummaryRepresentation } from 'app/shared/models/workbasket-summary-representation';
import { LinksWorkbasketSummary } from 'app/shared/models/links-workbasket-summary';

import { ImportExportComponent } from 'app/administration/components/import-export/import-export.component';

import { WorkbasketDefinitionService } from 'app/administration/services/workbasket-definition.service';
import { ClassificationDefinitionService } from 'app/administration/services/classification-definition.service';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { OrientationService } from 'app/shared/services/orientation/orientation.service';
import { configureTests } from 'app/app.test.configuration';
import { Page } from 'app/shared/models/page';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { NgxsModule } from '@ngxs/store';
import { WorkbasketListToolbarComponent } from '../workbasket-list-toolbar/workbasket-list-toolbar.component';
import { WorkbasketListComponent } from './workbasket-list.component';

@Component({
  selector: 'taskana-dummy-detail',
  template: 'dummydetail'
})
class DummyDetailComponent {
}

@Component({
  selector: 'taskana-pagination',
  template: 'dummydetail'
})
class PaginationComponent {
  @Input()
  page: Page;

  @Output()
  workbasketsResourceChange = new EventEmitter<any>();

  @Output() changePage = new EventEmitter<any>();
}

function createWorkbasketSummary(workbasketId, key, name, domain, type, description, owner, custom1, custom2, custom3, custom4) {
  const workbasketSummary: WorkbasketSummary = {
    workbasketId,
    key,
    name,
    domain,
    type,
    description,
    owner,
    custom1,
    custom2,
    custom3,
    custom4
  };
  return workbasketSummary;
}
const workbasketSummaryResource: WorkbasketSummaryRepresentation = {
  workbaskets: [
    createWorkbasketSummary('1', 'key1', 'NAME1', '', 'PERSONAL',
      'description 1', 'owner1', '', '', '', ''),
    createWorkbasketSummary('2', 'key2', 'NAME2', '', 'PERSONAL',
      'description 2', 'owner2', '', '', '', ''),
  ],
  _links: new LinksWorkbasketSummary({ href: 'url' }),
  page: {}
};

describe('WorkbasketListComponent', () => {
  let component: WorkbasketListComponent;
  let fixture: ComponentFixture<WorkbasketListComponent>;
  let debugElement: any;
  let workbasketService: WorkbasketService;
  let workbasketSummarySpy;

  const routes: Routes = [
    { path: ':id', component: DummyDetailComponent, outlet: 'detail' },
    { path: 'workbaskets', component: DummyDetailComponent }
  ];

  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        declarations: [
          WorkbasketListComponent,
          DummyDetailComponent,
          WorkbasketListToolbarComponent,
          ImportExportComponent
        ],
        imports: [
          AngularSvgIconModule,
          HttpClientModule,
          RouterTestingModule.withRoutes(routes),
          NgxsModule.forRoot()
        ],
        providers: [
          WorkbasketService,
          WorkbasketDefinitionService,
          ClassificationDefinitionService,
          OrientationService,
          ImportExportService
        ]
      });
    };
    configureTests(configure).then(testBed => {
      fixture = TestBed.createComponent(WorkbasketListComponent);
      component = fixture.componentInstance;
      debugElement = fixture.debugElement.nativeElement;
      workbasketService = TestBed.get(WorkbasketService);
      const orientationService = TestBed.get(OrientationService);
      workbasketSummarySpy = spyOn(workbasketService, 'getWorkBasketsSummary').and.returnValue(of(workbasketSummaryResource));
      spyOn(workbasketService, 'getSelectedWorkBasket').and.returnValue(of('2'));
      spyOn(orientationService, 'getOrientation').and.returnValue(of(undefined));

      fixture.detectChanges();
      done();
    });
  });

  afterEach(() => {
    fixture.detectChanges();
    document.body.removeChild(debugElement);
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should have wb-action-toolbar, wb-search-bar, wb-list-container, wb-pagination,'
        + ' collapsedMenufilterWb and taskana-filter created in the html', () => {
    expect(debugElement.querySelector('#wb-action-toolbar')).toBeDefined();
    expect(debugElement.querySelector('#wb-search-bar')).toBeDefined();
    expect(debugElement.querySelector('#wb-pagination')).toBeDefined();
    expect(debugElement.querySelector('#wb-list-container')).toBeDefined();
    expect(debugElement.querySelector('#collapsedMenufilterWb')).toBeDefined();
    expect(debugElement.querySelector('taskana-filter')).toBeDefined();
  });

  it('should have rendered sort by: name, id, description, owner and type', () => {
    expect(debugElement.querySelector('#sort-by-name')).toBeDefined();
    expect(debugElement.querySelector('#sort-by-key')).toBeDefined();
    expect(debugElement.querySelector('#sort-by-description')).toBeDefined();
    expect(debugElement.querySelector('#sort-by-owner')).toBeDefined();
    expect(debugElement.querySelector('#sort-by-type')).toBeDefined();
  });
});
