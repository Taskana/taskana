import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { DebugElement } from '@angular/core';
import { AccessIdsService } from 'app/shared/services/access-ids/access-ids.service';
import { TypeAheadComponent } from './type-ahead.component';
import { BrowserModule, By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterModule } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs/internal/observable/of';
import { MatSelectModule } from '@angular/material/select';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatTooltipModule } from '@angular/material/tooltip';

const AccessIdsServiceSpy: Partial<AccessIdsService> = {
  getAccessItems: jest.fn().mockReturnValue(of()),
  searchForAccessId: jest.fn().mockReturnValue(of())
};

describe('TypeAheadComponent', () => {
  let component: TypeAheadComponent;
  let fixture: ComponentFixture<TypeAheadComponent>;
  let debugElement: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TypeAheadComponent],
      imports: [
        BrowserModule,
        RouterModule,
        RouterTestingModule,
        HttpClientTestingModule,
        MatSelectModule,
        MatAutocompleteModule,
        MatFormFieldModule,
        MatInputModule,
        MatTooltipModule,
        FormsModule,
        BrowserAnimationsModule
      ],
      providers: [{ provide: AccessIdsService, useValue: AccessIdsServiceSpy }]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TypeAheadComponent);
    debugElement = fixture.debugElement;
    component = fixture.debugElement.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should change value via the input field', async(() => {
    component.value = 'val_1';
    component.initializeDataSource();
    fixture.detectChanges();
    fixture.whenStable().then(() => {
      let input = debugElement.query(By.css('.typeahead__form-input'));
      let el = input.nativeElement;
      expect(el.value).toBe('val_1');
      el.value = 'val_2';
      el.dispatchEvent(new Event('input'));
      expect(component.value).toBe('val_2');
      component.initializeDataSource();
      expect(component.items.length).toBeNull;
    });
  }));
});
