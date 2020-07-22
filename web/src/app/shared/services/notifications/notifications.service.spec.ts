import { ComponentFixture, inject, TestBed } from '@angular/core/testing';
import { MAT_SNACK_BAR_DATA, MatSnackBarModule } from '@angular/material/snack-bar';
import { Overlay } from '@angular/cdk/overlay';
import { MAT_DIALOG_SCROLL_STRATEGY, MatDialogModule } from '@angular/material/dialog';

import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { BrowserDynamicTestingModule } from '@angular/platform-browser-dynamic/testing';
import { NotificationService } from './notification.service';
import { NOTIFICATION_TYPES } from '../../models/notifications';
import { ToastComponent } from '../../components/toast/toast.component';

describe('NotificationService', () => {
  let toastComponent: ToastComponent;
  let toastFixture: ComponentFixture<ToastComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ToastComponent],
      providers: [
        NotificationService,
        Overlay,
        { provide: MAT_DIALOG_SCROLL_STRATEGY },
        { provide: MAT_SNACK_BAR_DATA }
      ],
      imports: [MatSnackBarModule, MatDialogModule, NoopAnimationsModule]
    })
      .overrideModule(BrowserDynamicTestingModule, { set: { entryComponents: [ToastComponent] } })
      .compileComponents();
  });

  beforeEach(() => {
    toastFixture = TestBed.createComponent(ToastComponent);
    toastComponent = toastFixture.componentInstance;
    toastFixture.detectChanges();
  });

  it('should be created', inject([NotificationService], (service: NotificationService) => {
    expect(service).toBeTruthy();
  }));

  it('should apply the correct panelClasses for the different alerts', inject(
    [NotificationService],
    (service: NotificationService) => {
      let ref = service.showToast(NOTIFICATION_TYPES.INFO_ALERT);
      expect(ref.containerInstance.snackBarConfig.panelClass).toEqual(['white', 'background-darkgreen']);
      ref = service.showToast(NOTIFICATION_TYPES.DANGER_ALERT);
      expect(ref.containerInstance.snackBarConfig.panelClass).toEqual(['red', 'background-white']);
      ref = service.showToast(NOTIFICATION_TYPES.WARNING_ALERT);
      expect(ref.containerInstance.snackBarConfig.panelClass).toEqual(['brown', 'background-white']);
      ref = service.showToast(NOTIFICATION_TYPES.SUCCESS_ALERT);
      expect(ref.containerInstance.snackBarConfig.panelClass).toEqual(['white', 'background-bluegreen']);
    }
  ));
});
