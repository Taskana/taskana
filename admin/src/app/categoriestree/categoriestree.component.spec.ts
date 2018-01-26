import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CategoriestreeComponent } from './categoriestree.component';

describe('CategoriestreeComponent', () => {
  let component: CategoriestreeComponent;
  let fixture: ComponentFixture<CategoriestreeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CategoriestreeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CategoriestreeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should be created', () => {
    expect(component).toBeTruthy();
  });
});
