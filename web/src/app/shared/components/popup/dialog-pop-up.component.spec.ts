import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import {
  MAT_DIALOG_DATA,
  MAT_DIALOG_SCROLL_STRATEGY,
  MatDialog,
  MatDialogClose,
  MatDialogModule
} from '@angular/material/dialog';

import { DialogPopUpComponent } from './dialog-pop-up.component';

describe('PopupComponent', () => {
  let component: DialogPopUpComponent;
  let fixture: ComponentFixture<DialogPopUpComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DialogPopUpComponent],
      providers: [{ provide: MAT_DIALOG_SCROLL_STRATEGY }, { provide: MAT_DIALOG_DATA }],
      imports: [MatDialogModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogPopUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
