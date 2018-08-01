import { Component, OnInit } from '@angular/core';
import { TaskanaEngineService } from 'app/services/taskana-engine/taskana-engine.service';
import { UserInfoModel } from 'app/models/user-info';
import { expandDown } from '../../shared/animations/expand.animation';

@Component({
  selector: 'taskana-user-information',
  templateUrl: './user-information.component.html',
  styleUrls: ['./user-information.component.scss'],
  animations: [expandDown],
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
