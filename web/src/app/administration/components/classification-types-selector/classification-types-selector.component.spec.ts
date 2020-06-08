import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgxsModule } from '@ngxs/store';
import { MatRadioModule } from '@angular/material/radio';
import { ClassificationTypesSelectorComponent } from './classification-types-selector.component';

describe('ClassificationTypesSelectorComponent', () => {
  let component: ClassificationTypesSelectorComponent;
  let fixture: ComponentFixture<ClassificationTypesSelectorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [NgxsModule.forRoot(), MatRadioModule],
      declarations: [ClassificationTypesSelectorComponent],
      providers: []
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassificationTypesSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
