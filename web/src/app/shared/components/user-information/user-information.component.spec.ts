import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, DebugElement, Input } from '@angular/core';
import { UserInformationComponent } from './user-information.component';
import { BrowserModule, By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { KadaiEngineService } from '../../services/kadai-engine/kadai-engine.service';
import { KadaiEngineServiceMock } from '../../services/kadai-engine/kadai-engine.mock.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

jest.mock('angular-svg-icon');

@Component({ selector: 'svg-icon', template: '' })
class SvgIconStub {
  @Input() src;
  @Input() matTooltip;
}

describe('UserInformationComponent', () => {
  let component: UserInformationComponent;
  let fixture: ComponentFixture<UserInformationComponent>;
  let debugElement: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UserInformationComponent, SvgIconStub],
      imports: [BrowserModule, HttpClientTestingModule, NoopAnimationsModule],
      providers: [{ provide: KadaiEngineService, useClass: KadaiEngineServiceMock }]
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
    const button = debugElement.query(By.css('.user-info__button')).nativeElement;
    expect(button).toBeTruthy();
    button.click();
    expect(component.showRoles).toBe(true);
  });
});
