import { Routes } from '@angular/router';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { NavBarComponent } from './nav-bar.component';

import { SelectedRouteService } from 'app/services/selected-route/selected-route';

describe('NavBarComponent', () => {
  let component: NavBarComponent;
  let fixture: ComponentFixture<NavBarComponent>;
  let navBar;

  const routes: Routes = [
    { path: 'classifications', component: NavBarComponent }
  ];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NavBarComponent],
      imports: [
        AngularSvgIconModule,
        HttpClientModule,
        RouterTestingModule.withRoutes(routes),
      ],
      providers: [SelectedRouteService]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavBarComponent);
    component = fixture.componentInstance;
    navBar = fixture.debugElement.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`should have as title 'Taskana administration'`, (() => {
    expect(navBar.title).toEqual('Taskana administration');
  }));
});
