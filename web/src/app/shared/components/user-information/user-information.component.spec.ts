import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, DebugElement } from '@angular/core';
import { UserInformationComponent } from './user-information.component';
import { BrowserModule, By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TaskanaEngineService } from '../../services/taskana-engine/taskana-engine.service';
import { TaskanaEngineServiceMock } from '../../services/taskana-engine/taskana-engine.mock.service';
import { of } from 'rxjs/internal/observable/of';
import { expandDown } from '../../animations/expand.animation';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { fromEventPattern } from 'rxjs';

const TaskanaEngingeServiceSpy = jest.fn().mockImplementation(
  (): Partial<TaskanaEngineServiceMock> => ({
    hasRole: jest.fn().mockReturnValue(of()),
    isHistoryProviderEnabled: jest.fn().mockReturnValue(of())
  })
);

describe('UserInformationComponent', () => {
  let component: UserInformationComponent;
  let fixture: ComponentFixture<UserInformationComponent>;
  let debugElement: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UserInformationComponent],
      imports: [BrowserModule, AngularSvgIconModule, HttpClientTestingModule, BrowserAnimationsModule],
      providers: [{ provide: TaskanaEngineService, useClass: TaskanaEngingeServiceSpy }]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserInformationComponent);
    debugElement = fixture.debugElement;
    component = fixture.debugElement.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle roles when roles clicked', () => {
    fixture.detectChanges();
    expect(component.showRoles).toBe(false);
    const button = debugElement.query(By.css('button')).nativeElement;
    expect(button).toBeTruthy();
    button.click();
    expect(component.showRoles).toBe(true);
  });
});
