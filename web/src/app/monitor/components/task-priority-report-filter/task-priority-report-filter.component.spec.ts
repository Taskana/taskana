import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { DebugElement } from '@angular/core';
import { NgxsModule, Store } from '@ngxs/store';
import { settingsStateMock } from '../../../shared/store/mock-data/mock-store';
import { SettingsState } from '../../../shared/store/settings-store/settings.state';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TaskPriorityReportFilterComponent } from './task-priority-report-filter.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatExpansionModule } from '@angular/material/expansion';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatDialogModule } from '@angular/material/dialog';

describe('TaskPriorityReportFilterComponent', () => {
  let fixture: ComponentFixture<TaskPriorityReportFilterComponent>;
  let debugElement: DebugElement;
  let component: TaskPriorityReportFilterComponent;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          NgxsModule.forRoot([SettingsState]),
          HttpClientTestingModule,
          MatCheckboxModule,
          MatExpansionModule,
          NoopAnimationsModule,
          MatDialogModule
        ],
        declarations: [TaskPriorityReportFilterComponent]
      }).compileComponents();

      fixture = TestBed.createComponent(TaskPriorityReportFilterComponent);
      debugElement = fixture.debugElement;
      component = fixture.debugElement.componentInstance;
      const store: Store = TestBed.inject(Store);
      store.reset({
        ...store.snapshot(),
        settings: settingsStateMock
      });
      fixture.detectChanges();
    })
  );

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should append filter name to activeFilters list when it is selected', () => {
    component.activeFilters = ['Tasks with state READY'];
    component.emitFilter(true, 'Tasks with state CLAIMED');
    expect(component.activeFilters).toStrictEqual(['Tasks with state READY', 'Tasks with state CLAIMED']);
  });

  it('should remove filter name from list when it is not selected anymore', () => {
    component.activeFilters = ['Tasks with state READY', 'Tasks with state CLAIMED'];
    component.emitFilter(false, 'Tasks with state CLAIMED');
    expect(component.activeFilters).toStrictEqual(['Tasks with state READY']);
  });

  it('should emit query according to values in activeFilters', () => {
    const emitSpy = jest.spyOn(component.applyFilter, 'emit');
    component.activeFilters = ['Tasks with state READY'];
    component.emitFilter(true, 'Tasks with state CLAIMED');
    expect(emitSpy).toBeCalledWith({ state: ['READY', 'CLAIMED'] });
  });
});
