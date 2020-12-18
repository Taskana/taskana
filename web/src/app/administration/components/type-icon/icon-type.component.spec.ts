import { Component, DebugElement, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { IconTypeComponent } from './icon-type.component';
import { SvgIconComponent, SvgIconRegistryService } from 'angular-svg-icon';
import { WorkbasketType } from '../../../shared/models/workbasket-type';

@Component({ selector: 'svg-icon', template: '' })
class SvgIconStub {
  @Input() src;
}

describe('IconTypeComponent', () => {
  let fixture: ComponentFixture<IconTypeComponent>;
  let debugElement: DebugElement;
  let component: IconTypeComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [],
      declarations: [IconTypeComponent, SvgIconStub],
      providers: []
    }).compileComponents();

    fixture = TestBed.createComponent(IconTypeComponent);
    debugElement = fixture.debugElement;
    component = fixture.debugElement.componentInstance;
    fixture.detectChanges();
  }));

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should return icon path dependent on the type when calling getIconPath', () => {
    expect(component.getIconPath(WorkbasketType.PERSONAL)).toBe('user.svg');
    expect(component.getIconPath(WorkbasketType.GROUP)).toBe('users.svg');
    expect(component.getIconPath(WorkbasketType.TOPIC)).toBe('topic.svg');
    expect(component.getIconPath(WorkbasketType.CLEARANCE)).toBe('clearance.svg');
    expect(component.getIconPath(undefined)).toBe('asterisk.svg');
  });

  it('should display svg-icon', () => {
    expect(debugElement.nativeElement.querySelector('svg-icon')).toBeTruthy();
  });
});
