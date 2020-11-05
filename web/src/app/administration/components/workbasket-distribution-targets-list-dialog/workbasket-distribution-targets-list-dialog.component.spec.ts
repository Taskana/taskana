import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkbasketDistributionTargetsListDialogComponent } from './workbasket-distribution-targets-list-dialog.component';

describe('WorkbasketDistributionTargetsListDialogComponent', () => {
  let component: WorkbasketDistributionTargetsListDialogComponent;
  let fixture: ComponentFixture<WorkbasketDistributionTargetsListDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkbasketDistributionTargetsListDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkbasketDistributionTargetsListDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
