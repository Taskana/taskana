import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, DebugElement } from '@angular/core';
import { NavBarComponent } from './nav-bar.component';
import { SelectedRouteService } from '../../services/selected-route/selected-route';
import { SidenavService } from '../../services/sidenav/sidenav.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { By } from '@angular/platform-browser';
import { of } from 'rxjs/internal/observable/of';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';

const SidenavServiceSpy = jest.fn().mockImplementation(
  (): Partial<SidenavService> => ({
    toggleSidenav: jest.fn().mockReturnValue(of())
  })
);

const SelectedRouteServiceSpy = jest.fn().mockImplementation(
  (): Partial<SelectedRouteService> => ({
    getSelectedRoute: jest.fn().mockReturnValue(of())
  })
);

@Component({ selector: 'svg-icon', template: '' })
class SvgIconStub {}

describe('NavBarComponent', () => {
  let component: NavBarComponent;
  let fixture: ComponentFixture<NavBarComponent>;
  let debugElement: DebugElement;
  var route = '';

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NavBarComponent, SvgIconStub],
      imports: [MatIconModule, HttpClientTestingModule, MatToolbarModule],
      providers: [
        { provide: SidenavService, useClass: SidenavServiceSpy },
        { provide: SelectedRouteService, useClass: SelectedRouteServiceSpy }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavBarComponent);
    debugElement = fixture.debugElement;
    component = fixture.debugElement.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set title to workbasket if workbasket ist selected', () => {
    route = 'workbaskets';
    fixture.detectChanges();
    component.setTitle(route);
    expect(component.title).toBe('Workbaskets');
  });

  it('should toggle sidenav when button clicked', () => {
    fixture.detectChanges();
    expect(component.toggle).toBe(false);
    const button = debugElement.query(By.css('.navbar_button-toggle')).nativeElement;
    expect(button).toBeTruthy();
    button.click();
    expect(component.toggle).toBe(true);
  });
});
