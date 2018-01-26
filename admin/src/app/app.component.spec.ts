import { TestBed, async, inject, tick, fakeAsync } from '@angular/core/testing';

import { AppComponent } from './app.component';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientModule } from '@angular/common/http';
import { Router, Routes } from '@angular/router';

describe('AppComponent', () => {

  var app, fixture, debugElement;

  const routes: Routes = [
    { path: 'categories', component: AppComponent }
  ];


  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent
      ],
      imports:[
        AngularSvgIconModule,
        RouterTestingModule.withRoutes(routes),
        HttpClientModule
        
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    app = fixture.debugElement.componentInstance;
    debugElement = fixture.debugElement.nativeElement;
    
  }));

  afterEach(async(()=>{
    document.body.removeChild(debugElement);
  }));

  it('should create the app', async(() => {
    expect(app).toBeTruthy();
  }));

  it(`should have as title 'Taskana administration'`, async(() => {
    expect(app.title).toEqual('Taskana administration');
  }));

  it('should render title in a <a> tag', async(() => {
    fixture.detectChanges();
    expect(debugElement.querySelector('ul p a').textContent).toContain('Taskana administration');
  }));

  it('should call Router.navigateByUrl("categories") and workbasketRoute should be false', async (inject([Router], (router: Router) => {
    
    expect(app.workbasketsRoute).toBe(true);
    fixture.detectChanges();
    router.navigateByUrl(`/categories`);
    expect(app.workbasketsRoute).toBe(false);
    
  })));
})
