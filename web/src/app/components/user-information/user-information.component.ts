import { Component, OnInit } from '@angular/core';
import { TaskanaEngineService } from 'app/services/taskana-engine/taskana-engine.service';
import { UserInfoModel } from 'app/models/user-info';
import { trigger, transition, animate, keyframes, style } from '@angular/animations';

@Component({
  selector: 'taskana-user-information',
  templateUrl: './user-information.component.html',
  styleUrls: ['./user-information.component.scss'],
  animations: [
    trigger('toggle', [
      transition('void => *', animate('300ms ease-in', keyframes([
        style({ opacity: 0, height: '0px' }),
        style({ opacity: 1, height: '10px' }),
        style({ opacity: 1, height: '*' })]))),
      transition('* => void', animate('300ms ease-out', keyframes([
        style({ opacity: 1, height: '*' }),
        style({ opacity: 0, height: '10px' }),
        style({ opacity: 0, height: '0px' })])))
    ]
    )],
})
export class UserInformationComponent implements OnInit {

  userInformation: UserInfoModel;
  roles = '';
  showRoles = false;
  constructor(private taskanaEngineService: TaskanaEngineService) { }

  ngOnInit() {
    this.userInformation = this.taskanaEngineService.currentUserInfo;
    if (this.userInformation) {
      this.roles = '[' + this.userInformation.roles.join(',') + ']';
    }
  }

  toogleRoles() {
    this.showRoles = !this.showRoles;
  }
}
