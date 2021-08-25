import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { Component, DebugElement, Input, Pipe, PipeTransform } from '@angular/core';
import { WorkbasketDistributionTargetsListComponent } from './workbasket-distribution-targets-list.component';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { WorkbasketType } from '../../../shared/models/workbasket-type';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { workbasketReadStateMock } from '../../../shared/store/mock-data/mock-store';
import { Side } from '../workbasket-distribution-targets/workbasket-distribution-targets.component';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({ selector: 'taskana-shared-workbasket-filter', template: '' })
class FilterStub {
  @Input() component = 'availableDistributionTargetList';
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

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          MatIconModule,
          MatToolbarModule,
          MatListModule,
          MatTooltipModule,
          InfiniteScrollModule,
          BrowserAnimationsModule
        ],
        declarations: [WorkbasketDistributionTargetsListComponent, FilterStub, SpinnerStub, IconTypeStub, OrderByMock],
        providers: []
      }).compileComponents();

      fixture = TestBed.createComponent(WorkbasketDistributionTargetsListComponent);
      debugElement = fixture.debugElement;
      component = fixture.componentInstance;
      component.distributionTargets = workbasketReadStateMock.paginatedWorkbasketsSummary.workbaskets;
      component.side = Side.AVAILABLE;
    })
  );

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
    expect(debugElement.nativeElement.querySelector('taskana-shared-workbasket-filter')).toBeTruthy();
  });

  it('should display all available workbaskets', () => {
    fixture.detectChanges();
    const distributionTargetList = debugElement.nativeElement.getElementsByClassName(
      'workbasket-distribution-targets__workbaskets-item'
    );
    expect(distributionTargetList).toHaveLength(5);
  });

  it('should call orderBy pipe', () => {
    const orderBySpy = jest.spyOn(OrderByMock.prototype, 'transform');
    fixture.detectChanges();
    expect(orderBySpy).toHaveBeenCalledWith(component.distributionTargets, ['type', 'name']);
  });
});
