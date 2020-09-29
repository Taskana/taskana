import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdministrationOverviewComponent } from './administration-overview.component';

describe('AdministrationOverviewComponent', () => {
  let component: AdministrationOverviewComponent;
  let fixture: ComponentFixture<AdministrationOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdministrationOverviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdministrationOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
