import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { WorkbasketSummary } from 'app/models/workbasket-summary';
import { WorkbasketSummaryResource } from 'app/models/workbasket-summary-resource';
import { FilterModel } from 'app/models/filter';
import { LinksWorkbasketSummary } from 'app/models/links-workbasket-summary';

import { ImportExportComponent } from 'app/administration/components/import-export/import-export.component';

import { WorkbasketDefinitionService } from 'app/administration/services/workbasket-definition.service';
import { ClassificationDefinitionService } from 'app/administration/services/classification-definition.service';
import { WorkbasketService } from 'app/shared/services/workbasket/workbasket.service';
import { OrientationService } from 'app/services/orientation/orientation.service';
import { configureTests } from 'app/app.test.configuration';
import { Page } from 'app/models/page';
import { ImportExportService } from 'app/administration/services/import-export.service';
import { WorkbasketListToolbarComponent } from './workbasket-list-toolbar/workbasket-list-toolbar.component';
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

const workbasketSummaryResource: WorkbasketSummaryResource = new WorkbasketSummaryResource(
  new Array<WorkbasketSummary>(
    new WorkbasketSummary('1', 'key1', 'NAME1', 'description 1', 'owner 1', '', '', 'PERSONAL', '', '', '', ''),
    new WorkbasketSummary('2', 'key2', 'NAME2', 'description 2', 'owner 2', '', '', 'GROUP', '', '', '', '')
  ),
  new LinksWorkbasketSummary({ href: 'url' })
);


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
          RouterTestingModule.withRoutes(routes)
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

  it('should call workbasketService.getWorkbasketsSummary method on init', () => {
    component.ngOnInit();
    expect(workbasketService.getWorkBasketsSummary).toHaveBeenCalled();
    workbasketService.getWorkBasketsSummary().subscribe(value => {
      expect(value).toBe(workbasketSummaryResource);
    });
  });

  it('should have wb-action-toolbar, wb-search-bar, wb-list-container, wb-pagination,'
        + ' collapsedMenufilterWb and taskana-filter created in the html', () => {
    expect(debugElement.querySelector('#wb-action-toolbar')).toBeDefined();
    expect(debugElement.querySelector('#wb-search-bar')).toBeDefined();
    expect(debugElement.querySelector('#wb-pagination')).toBeDefined();
    expect(debugElement.querySelector('#wb-list-container')).toBeDefined();
    expect(debugElement.querySelector('#collapsedMenufilterWb')).toBeDefined();
    expect(debugElement.querySelector('taskana-filter')).toBeDefined();
    expect(debugElement.querySelectorAll('#wb-list-container > li').length).toBe(3);
  });

  // it('should have two workbasketsummary rows created with the second one selected.', fakeAsync(() => {
  //   tick(0);
  //   fixture.detectChanges();
  //   fixture.whenStable().then(() => {
  //     expect(debugElement.querySelectorAll('#wb-list-container > li').length).toBe(3);
  //     expect(debugElement.querySelectorAll('#wb-list-container > li')[1].getAttribute('class'))
  //       .toBe('list-group-item ng-star-inserted');
  //     expect(debugElement.querySelectorAll('#wb-list-container > li')[2].getAttribute('class'))
  //       .toBe('list-group-item ng-star-inserted active');
  //   })
  //
  // }));

  it('should have two workbasketsummary rows created with two different icons: user and users', () => {
    expect(debugElement.querySelectorAll('#wb-list-container > li')[1]
      .querySelector('svg-icon').getAttribute('ng-reflect-src')).toBe('./assets/icons/user.svg');
    expect(debugElement.querySelectorAll('#wb-list-container > li')[2]
      .querySelector('svg-icon').getAttribute('ng-reflect-src')).toBe('./assets/icons/users.svg');
  });

  it('should have rendered sort by: name, id, description, owner and type', () => {
    expect(debugElement.querySelector('#sort-by-name')).toBeDefined();
    expect(debugElement.querySelector('#sort-by-key')).toBeDefined();
    expect(debugElement.querySelector('#sort-by-description')).toBeDefined();
    expect(debugElement.querySelector('#sort-by-owner')).toBeDefined();
    expect(debugElement.querySelector('#sort-by-type')).toBeDefined();
  });

  it('should have performRequest with forced = true after performFilter is triggered', (() => {
    const filter = new FilterModel({
      name: 'someName',
      owner: 'someOwner',
      description: 'someDescription',
      key: 'someKey',
      type: 'PERSONAL'
    });
    component.performFilter(filter);

    expect(workbasketSummarySpy.calls.all()[1].args).toEqual([true, 'name', 'asc',
      '', 'someName', 'someDescription', '', 'someOwner', 'PERSONAL', '', 'someKey', '']);
  }));
});
