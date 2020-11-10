import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDividerModule } from '@angular/material/divider';
import { AccessItemsManagementDialogComponent } from './access-items-management-dialog.component';
import { MatListModule } from '@angular/material/list';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('AccessItemsManagementDialogComponent', () => {
  let component: AccessItemsManagementDialogComponent;
  let fixture: ComponentFixture<AccessItemsManagementDialogComponent>;

  beforeEach(async(() => {
    const mockDialogRef = {
      close: jasmine.createSpy('close')
    };
    TestBed.configureTestingModule({
      imports: [MatDividerModule, MatListModule, MatDialogModule],
      declarations: [AccessItemsManagementDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: { mockDialogRef } },
        { provide: MAT_DIALOG_DATA, useValue: {} }
      ]
    }).compileComponents();
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
