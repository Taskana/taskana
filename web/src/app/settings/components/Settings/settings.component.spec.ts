import { DebugElement } from '@angular/core';
import { Observable } from 'rxjs';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { Actions, NgxsModule, ofActionDispatched, Store } from '@ngxs/store';
import { FormsModule } from '@angular/forms';
import { NotificationService } from '../../../shared/services/notifications/notification.service';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatTooltipModule } from '@angular/material/tooltip';
import { SettingsState } from '../../../shared/store/settings-store/settings.state';
import { SettingsComponent } from './settings.component';
import { settingsStateMock } from '../../../shared/store/mock-data/mock-store';
import { SetSettings } from '../../../shared/store/settings-store/settings.actions';
import { HttpClientTestingModule } from '@angular/common/http/testing';

const notificationServiceSpy: Partial<NotificationService> = {
  showError: jest.fn(),
  showSuccess: jest.fn(),
  showDialog: jest.fn()
};

describe('SettingsComponent', () => {
  let fixture: ComponentFixture<SettingsComponent>;
  let debugElement: DebugElement;
  let component: SettingsComponent;
  let store: Store;
  let actions$: Observable<any>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          NgxsModule.forRoot([SettingsState]),
          HttpClientTestingModule,
          FormsModule,
          MatIconModule,
          MatFormFieldModule,
          MatInputModule,
          MatTooltipModule,
          BrowserAnimationsModule
        ],
        declarations: [SettingsComponent],
        providers: [{ provide: NotificationService, useValue: notificationServiceSpy }]
      }).compileComponents();

      fixture = TestBed.createComponent(SettingsComponent);
      debugElement = fixture.debugElement;
      component = fixture.debugElement.componentInstance;
      store = TestBed.inject(Store);
      actions$ = TestBed.inject(Actions);
      store.reset({
        ...store.snapshot(),
        settings: settingsStateMock
      });
      fixture.detectChanges();
    })
  );

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should show success when form is saved successfully', () => {
    const showSuccessSpy = jest.spyOn(notificationServiceSpy, 'showSuccess');
    component.onSave();
    expect(showSuccessSpy).toHaveBeenCalled();
  });

  it('should show error when an invalid form is tried to be saved', () => {
    component.settings['intervalHighPriority'] = [-100, 100];
    const showErrorSpy = jest.spyOn(notificationServiceSpy, 'showError');
    component.onSave();
    expect(showErrorSpy).toHaveBeenCalled();
  });

  it('should dispatch action onValidate() returns true', async () => {
    let isActionDispatched = false;
    actions$.pipe(ofActionDispatched(SetSettings)).subscribe(() => (isActionDispatched = true));
    component.onSave();
    expect(isActionDispatched).toBe(true);
  });
});
