import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { IconTypeComponent } from './icon-type.component';
import { configureTests } from 'app/app.test.configuration';

describe('IconTypeComponent', () => {
  let component: IconTypeComponent;
  let fixture: ComponentFixture<IconTypeComponent>;
  let debugElement;

  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        imports: [AngularSvgIconModule, HttpClientModule]
      })
    };
    configureTests(configure).then(testBed => {
      fixture = TestBed.createComponent(IconTypeComponent);
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
