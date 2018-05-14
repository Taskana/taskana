import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { HttpModule } from '@angular/http';

import { UserInformationComponent } from './user-information.component';

import { TaskanaEngineService } from 'app/services/taskana-engine/taskana-engine.service';
import { UserInfoModel } from 'app/models/user-info';

describe('UserInformationComponent', () => {
  let component: UserInformationComponent;
  let fixture: ComponentFixture<UserInformationComponent>;
  let taskanaEngineService;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [AngularSvgIconModule,
        HttpClientModule, HttpModule],
      declarations: [UserInformationComponent],
      providers: [TaskanaEngineService]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserInformationComponent);
    component = fixture.componentInstance;
    taskanaEngineService = TestBed.get(TaskanaEngineService);
		spyOn(taskanaEngineService, 'getUserInformation').and.returnValue(Observable.of(new UserInfoModel));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
