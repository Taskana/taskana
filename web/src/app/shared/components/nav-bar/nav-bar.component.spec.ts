import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { DebugElement } from '@angular/core';
import { NavBarComponent } from './nav-bar.component';
import { SelectedRouteService } from 'app/shared/services/selected-route/selected-route';
import { MatIconModule } from '@angular/material';
import { SidenavService } from '../../../shared/services/sidenav/sidenav.service';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { By } from '@angular/platform-browser';
import { of } from 'rxjs/internal/observable/of';

const SidenavServiceSpy = jest.fn().mockImplementation(
  (): Partial<SidenavService> => ({
    toggle_sidenav: jest.fn().mockReturnValue(of())
  })
);

const SelectedRouteServiceSpy = jest.fn().mockImplementation(
  (): Partial<SelectedRouteService> => ({
    getSelectedRoute: jest.fn().mockReturnValue(of())
  })
);

describe('SidenavListComponent', () => {
  let component: NavBarComponent;
  let fixture: ComponentFixture<NavBarComponent>;
  let debugElement: DebugElement;
  var route = '';

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NavBarComponent],
      imports: [MatIconModule, HttpClientTestingModule, AngularSvgIconModule],
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
    const button = debugElement.query(By.css('button')).nativeElement;
    expect(button).toBeTruthy();
    button.click();
    expect(component.toggle).toBe(true);
  });
});
