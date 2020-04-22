import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AlertComponent } from './alert.component';
import { NOTIFICATION_TYPES } from '../../models/notifications';
import { NotificationService } from '../../services/notifications/notification.service';

// TODO re-enable these tests when alert-component has been refactored and renamed (message component)
xdescribe('AlertComponent', () => {
  let component: AlertComponent;
  let fixture: ComponentFixture<AlertComponent>;
  let debugElement;
  let notificationsService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [BrowserAnimationsModule],
      declarations: [AlertComponent],
      providers: [NotificationService]
    }).compileComponents();
  }));

  beforeEach(() => {
    notificationsService = TestBed.get(NotificationService);
    fixture = TestBed.createComponent(AlertComponent);
    component = fixture.componentInstance;
    debugElement = fixture.debugElement.nativeElement;
    fixture.detectChanges();
  });

  afterEach(() => {
    document.body.removeChild(debugElement);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show alert message', () => {
    notificationsService.triggerAlert(NOTIFICATION_TYPES.SUCCESS_ALERT);
    fixture.detectChanges();
    expect(debugElement.querySelector('#alert-icon').innerText).toBe('done');
    expect(debugElement.querySelector('#alert-text').innerText).toBe('some custom text');
  });

  it('should have differents alert types', () => {
    notificationsService.triggerAlert(NOTIFICATION_TYPES.WARNING_ALERT);
    fixture.detectChanges();
    expect(debugElement.querySelector('#alert-icon').innerText).toBe('warning');

    notificationsService.triggerAlert(NOTIFICATION_TYPES.SUCCESS_ALERT);
    fixture.detectChanges();
    expect(debugElement.querySelector('#alert-icon').innerText).toBe('warning');
    expect(debugElement.querySelector('#alert-text').innerText).toBe('some custom text');
  });
});
