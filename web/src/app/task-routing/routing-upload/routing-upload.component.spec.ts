import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoutingUploadComponent } from './routing-upload.component';

describe('RoutingUploadComponent', () => {
  let component: RoutingUploadComponent;
  let fixture: ComponentFixture<RoutingUploadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RoutingUploadComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RoutingUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
