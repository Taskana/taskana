import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RemoveConfirmationComponent } from './remove-confirmation.component';
import { configureTests } from 'app/app.test.configuration';
import { RemoveConfirmationService } from '../../services/remove-confirmation/remove-confirmation.service';

describe('RemoveConfirmationComponent', () => {
  let component: RemoveConfirmationComponent;
  let fixture: ComponentFixture<RemoveConfirmationComponent>;
  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        declarations: [],
        providers: []
      })
    };
    configureTests(configure).then(testBed => {
      fixture = TestBed.createComponent(RemoveConfirmationComponent);
      component = fixture.componentInstance;
      TestBed.get(RemoveConfirmationService);
      fixture.detectChanges();
      done();
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
