import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';

import { UserInformationComponent } from './user-information.component';

import { configureTests } from 'app/app.test.configuration';

describe('UserInformationComponent', () => {
  let component: UserInformationComponent;
  let fixture: ComponentFixture<UserInformationComponent>;
  let debugElement;


  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        imports: [AngularSvgIconModule,
          HttpClientModule],
        declarations: [UserInformationComponent],
      })
    };
    configureTests(configure).then(testBed => {
      fixture = TestBed.createComponent(UserInformationComponent);
      component = fixture.componentInstance;
      debugElement = fixture.debugElement.nativeElement;
      fixture.detectChanges();
      done();
    });

  });

  afterEach(() => {
    document.body.removeChild(debugElement);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
