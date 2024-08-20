import { ComponentFixture, fakeAsync, flush, TestBed, waitForAsync } from '@angular/core/testing';
import { Component, DebugElement, Input, Pipe, PipeTransform } from '@angular/core';
import { WorkbasketDistributionTargetsListComponent } from './workbasket-distribution-targets-list.component';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { WorkbasketType } from '../../../shared/models/workbasket-type';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { engineConfigurationMock, workbasketReadStateMock } from '../../../shared/store/mock-data/mock-store';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Side } from '../../models/workbasket-distribution-enums';
import { NgxsModule, Store } from '@ngxs/store';
import { WorkbasketState } from '../../../shared/store/workbasket-store/workbasket.state';
import { animationFrameScheduler, EMPTY, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { MatDialogModule } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { RequestInProgressService } from '../../../shared/services/request-in-progress/request-in-progress.service';
import { ScrollingModule } from '@angular/cdk/scrolling';

@Component({ selector: 'kadai-shared-workbasket-filter', template: '' })
class FilterStub {
  @Input() component = 'availableDistributionTargetList';
}

@Component({ selector: 'kadai-shared-spinner', template: '' })
class SpinnerStub {
  @Input() isRunning: boolean;
}

@Component({ selector: 'kadai-administration-icon-type', template: '' })
class IconTypeStub {
  @Input() type: WorkbasketType;
  @Input() text: string;
}

@Pipe({ name: 'orderBy' })
class OrderByMock implements PipeTransform {
  transform(list, sortBy): any {
    return list;
  }
}

describe('WorkbasketDistributionTargetsListComponent', () => {
  let fixture: ComponentFixture<WorkbasketDistributionTargetsListComponent>;
  let debugElement: DebugElement;
  let component: WorkbasketDistributionTargetsListComponent;
  let store: Store;

  const routeParamsMock = { id: 'workbasket' };
  const activatedRouteMock = {
    firstChild: {
      params: of(routeParamsMock)
    }
  };

  const httpSpy = jest.fn().mockImplementation(
    (): Partial<HttpClient> => ({
      get: jest.fn().mockReturnValue(of([])),
      post: jest.fn().mockReturnValue(of([]))
    })
  );

  const domainServiceSpy: Partial<DomainService> = {
    getSelectedDomainValue: jest.fn().mockReturnValue(of(null)),
    getSelectedDomain: jest.fn().mockReturnValue(of('A')),
    getDomains: jest.fn().mockReturnValue(of(null))
  };

  const requestInProgressServiceSpy: Partial<RequestInProgressService> = {
    setRequestInProgress: jest.fn().mockReturnValue(of(null))
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        MatIconModule,
        MatToolbarModule,
        MatListModule,
        MatDialogModule,
        MatTooltipModule,
        InfiniteScrollModule,
        ScrollingModule,
        NoopAnimationsModule,
        NgxsModule.forRoot([WorkbasketState])
      ],
      declarations: [WorkbasketDistributionTargetsListComponent, FilterStub, SpinnerStub, IconTypeStub, OrderByMock],
      providers: [
        { provide: HttpClient, useValue: httpSpy },
        {
          provide: DomainService,
          useValue: domainServiceSpy
        },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: RequestInProgressService, useValue: requestInProgressServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(WorkbasketDistributionTargetsListComponent);
    debugElement = fixture.debugElement;
    component = fixture.componentInstance;
    component.distributionTargets = workbasketReadStateMock.paginatedWorkbasketsSummary.workbaskets;
    component.side = Side.AVAILABLE;
    component.transferDistributionTargetObservable = EMPTY;
    store = TestBed.inject(Store);
    store.reset({
      ...store.snapshot(),
      engineConfiguration: engineConfigurationMock,
      workbasket: workbasketReadStateMock
    });
  }));

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should set sideNumber to 0 when side is Side.AVAILABLE', () => {
    fixture.detectChanges();
    expect(component.side).toBe(Side.AVAILABLE);
  });

  it('should change toolbar state', () => {
    expect(component.toolbarState).toBe(false);
    component.changeToolbarState(true);
    expect(component.toolbarState).toBe(true);
  });

  it('should display filter when toolbarState is true', () => {
    component.toolbarState = true;
    fixture.detectChanges();
    expect(debugElement.nativeElement.querySelector('kadai-shared-workbasket-filter')).toBeTruthy();
  });

  it('should display all available workbaskets', fakeAsync(() => {
    // On the first cycle we render the items.
    fixture.detectChanges();
    flush();
    // Flush the initial fake scroll event.
    animationFrameScheduler.flush();
    flush();
    fixture.detectChanges();

    const distributionTargetList = debugElement.nativeElement.getElementsByClassName(
      'workbasket-distribution-targets__workbaskets-item'
    );
    expect(distributionTargetList).toHaveLength(3);
  }));

  it('should call orderBy pipe', () => {
    const orderBySpy = jest.spyOn(OrderByMock.prototype, 'transform');
    fixture.detectChanges();
    expect(orderBySpy).toHaveBeenCalledWith(component.distributionTargets, ['name']);
  });
});
