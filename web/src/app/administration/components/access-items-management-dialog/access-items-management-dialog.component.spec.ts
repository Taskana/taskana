import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccessItemsManagementDialogComponent } from './access-items-management-dialog.component';

describe('AccessItemsManagementDialogComponent', () => {
  let component: AccessItemsManagementDialogComponent;
  let fixture: ComponentFixture<AccessItemsManagementDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AccessItemsManagementDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccessItemsManagementDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
