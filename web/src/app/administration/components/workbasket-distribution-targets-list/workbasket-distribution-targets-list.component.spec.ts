import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, DebugElement, EventEmitter, Input, Output } from '@angular/core';
import { WorkbasketDistributionTargetsListComponent } from './workbasket-distribution-targets-list.component';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { WorkbasketType } from '../../../shared/models/workbasket-type';
import { SelectWorkBasketPipe } from '../../../shared/pipes/select-workbaskets.pipe';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { workbasketReadStateMock } from '../../../shared/store/mock-data/mock-store';
import { Side } from '../workbasket-distribution-targets/workbasket-distribution-targets.component';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { WorkbasketQueryFilterParameter } from '../../../shared/models/workbasket-query-filter-parameter';

@Component({ selector: 'taskana-shared-workbasket-filter', template: '' })
class FilterStub {
  @Output() performFilter = new EventEmitter<WorkbasketQueryFilterParameter>();
}

@Component({ selector: 'taskana-shared-spinner', template: '' })
class SpinnerStub {
  @Input() isRunning: boolean;
}

@Component({ selector: 'taskana-administration-icon-type', template: '' })
class IconTypeStub {
  @Input() type: WorkbasketType;
  @Input() text: string;
}

describe('WorkbasketDistributionTargetsListComponent', () => {
  let fixture: ComponentFixture<WorkbasketDistributionTargetsListComponent>;
  let debugElement: DebugElement;
  let component: WorkbasketDistributionTargetsListComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MatIconModule, MatToolbarModule, MatListModule, InfiniteScrollModule, BrowserAnimationsModule],
      declarations: [
        WorkbasketDistributionTargetsListComponent,
        FilterStub,
        SpinnerStub,
        IconTypeStub,
        SelectWorkBasketPipe
      ],
      providers: []
    }).compileComponents();

    fixture = TestBed.createComponent(WorkbasketDistributionTargetsListComponent);
    debugElement = fixture.debugElement;
    component = fixture.componentInstance;
    component.distributionTargets = workbasketReadStateMock.paginatedWorkbasketsSummary.workbaskets;
    component.distributionTargetsSelected = [];
    component.side = Side.AVAILABLE;
  }));

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should set sideNumber to 0 when side is Side.AVAILABLE', () => {
    fixture.detectChanges();
    expect(component.side).toBe(Side.AVAILABLE);
  });

  it('should select all distribution targets', () => {
    component.selectAll(true);
    component.distributionTargets.forEach((element) => {
      expect(element['selected']).toBe(true);
    });
  });

  it('should emit side when scrolling', () => {
    const scrollingEmitSpy = jest.spyOn(component.scrolling, 'emit');
    component.onScroll();
    expect(scrollingEmitSpy).toHaveBeenCalledWith(component.side);
  });

  it('should change toolbar state', () => {
    expect(component.toolbarState).toBe(false);
    component.changeToolbarState(true);
    expect(component.toolbarState).toBe(true);
  });

  it('should display all available workbaskets', () => {
    fixture.detectChanges();
    const distributionTargetList = debugElement.nativeElement.getElementsByClassName(
      'workbasket-distribution-targets__workbaskets-item'
    );
    expect(distributionTargetList).toHaveLength(5);
  });
});
