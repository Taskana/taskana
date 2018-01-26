import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkbasketeditorComponent } from './workbasketeditor.component';

describe('WorkbasketeditorComponent', () => {
  let component: WorkbasketeditorComponent;
  let fixture: ComponentFixture<WorkbasketeditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkbasketeditorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkbasketeditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should be created', () => {
    expect(component).toBeTruthy();
  });
});
