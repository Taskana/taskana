import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, DebugElement } from '@angular/core';
import { SidenavListComponent } from './sidenav-list.component';
import { SidenavService } from '../../../shared/services/sidenav/sidenav.service';
import {
  MatButtonModule,
  MatSidenavModule,
  MatCheckboxModule,
  MatGridListModule,
  MatListModule,
  MatIconModule
} from '@angular/material';
import { BrowserModule, By } from '@angular/platform-browser';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterModule } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { TaskanaEngineService } from '../../services/taskana-engine/taskana-engine.service';
import { TaskanaEngineServiceMock } from '../../services/taskana-engine/taskana-engine.mock.service';
import { of } from 'rxjs/internal/observable/of';

const SidenavServiceSpy = jest.fn().mockImplementation(
  (): Partial<SidenavService> => ({
    toggle_sidenav: jest.fn().mockReturnValue(of())
  })
);

const TaskanaEngingeServiceSpy = jest.fn().mockImplementation(
  (): Partial<TaskanaEngineServiceMock> => ({
    hasRole: jest.fn().mockReturnValue(of()),
    isHistoryProviderEnabled: jest.fn().mockReturnValue(of())
  })
);

describe('SidenavListComponent', () => {
  let component: SidenavListComponent;
  let fixture: ComponentFixture<SidenavListComponent>;
  let debugElement: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SidenavListComponent],
      imports: [
        MatButtonModule,
        MatSidenavModule,
        MatCheckboxModule,
        MatGridListModule,
        MatListModule,
        MatIconModule,
        BrowserModule,
        RouterModule,
        RouterTestingModule,
        HttpClientTestingModule
      ],
      providers: [
        { provide: SidenavService, useClass: SidenavServiceSpy },
        { provide: TaskanaEngineService, useClass: TaskanaEngingeServiceSpy }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SidenavListComponent);
    debugElement = fixture.debugElement;
    component = fixture.debugElement.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all links if user has all permissions', () => {
    component.administrationAccess = true;
    component.monitorAccess = true;
    component.workplaceAccess = true;
    component.historyAccess = true;
    fixture.detectChanges();
    const menuList = debugElement.queryAll(By.css('.list-item'));
    expect(menuList.length).toBe(7);
    fixture.detectChanges();
  });

  it('should show all links if user has only monitor access', () => {
    component.administrationAccess = false;
    component.monitorAccess = true;
    component.workplaceAccess = false;
    component.historyAccess = false;
    fixture.detectChanges();
    const menuList = debugElement.queryAll(By.css('.list-item'));
    expect(menuList.length).toBe(1);
  });

  it('should toggle sidenav when link clicked', () => {
    component.toggle = true;
    fixture.detectChanges();
    const button = debugElement.query(By.css('.list-item__admin-workbaskets')).nativeElement;
    expect(button).toBeTruthy();
    button.click();
    expect(component.toggle).toBe(false);
  });
});
