import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, DebugElement, EventEmitter, Input, Output } from '@angular/core';
import { WorkbasketDualListComponent } from './workbasket-dual-list.component';
import { Filter } from '../../../shared/models/filter';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { ICONTYPES } from '../../../shared/models/icon-types';
import { SelectWorkBasketPipe } from '../../../shared/pipes/select-workbaskets.pipe';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { workbasketReadStateMock } from '../../../shared/store/mock-data/mock-store';
import { Side } from '../workbasket-distribution-targets/workbasket-distribution-targets.component';

@Component({ selector: 'taskana-shared-filter', template: '' })
class FilterStub {
  @Output() performFilter = new EventEmitter<Filter>();
}

@Component({ selector: 'taskana-shared-spinner', template: '' })
class SpinnerStub {
  @Input() isRunning: boolean;
}

@Component({ selector: 'taskana-administration-icon-type', template: '' })
class IconTypeStub {
  @Input() type: ICONTYPES = ICONTYPES.ALL;
  @Input() text: string;
}

describe('WorkbasketDualListComponent', () => {
  let fixture: ComponentFixture<WorkbasketDualListComponent>;
  let debugElement: DebugElement;
  let component: WorkbasketDualListComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [InfiniteScrollModule, BrowserAnimationsModule],
      declarations: [WorkbasketDualListComponent, FilterStub, SpinnerStub, IconTypeStub, SelectWorkBasketPipe],
      providers: []
    }).compileComponents();

    fixture = TestBed.createComponent(WorkbasketDualListComponent);
    debugElement = fixture.debugElement;
    component = fixture.componentInstance;
    component.distributionTargets = workbasketReadStateMock.paginatedWorkbasketsSummary.workbaskets;
    component.distributionTargetsSelected = [];
    component.side = Side.LEFT;
  }));

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should set sideNumber to 0 when side is Side.LEFT', () => {
    fixture.detectChanges();
    expect(component.sideNumber).toBe(0);
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

  it('should emit filter model and side when performing filter', () => {
    const performDualListFilterSpy = jest.spyOn(component.performDualListFilter, 'emit');
    const filterModelMock: Filter = { filterParams: 'filter' };
    component.performAvailableFilter(filterModelMock);
    expect(performDualListFilterSpy).toHaveBeenCalledWith({ filterBy: filterModelMock, side: component.side });
  });

  it('should change toolbar state', () => {
    expect(component.toolbarState).toBe(false);
    component.changeToolbarState(true);
    expect(component.toolbarState).toBe(true);
  });

  it('should display all available workbaskets', () => {
    fixture.detectChanges();
    const distributionTargetList = debugElement.nativeElement.getElementsByClassName(
      'workbasket-list__distribution-targets'
    );
    expect(distributionTargetList).toHaveLength(5);
  });
});
