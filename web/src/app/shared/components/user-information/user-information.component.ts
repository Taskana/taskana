import { Component, OnInit } from '@angular/core';
import { TaskanaEngineService } from 'app/shared/services/taskana-engine/taskana-engine.service';
import { UserInfo } from 'app/shared/models/user-info';
import { expandDown } from '../../animations/expand.animation';

@Component({
  selector: 'taskana-shared-user-information',
  templateUrl: './user-information.component.html',
  styleUrls: ['./user-information.component.scss'],
  animations: [expandDown]
})
export class UserInformationComponent implements OnInit {
  userInformation: UserInfo;
  roles = '';
  showRoles = false;
  constructor(private taskanaEngineService: TaskanaEngineService) {}

  ngOnInit() {
    this.userInformation = this.taskanaEngineService.currentUserInfo;
    if (this.userInformation) {
      this.roles = `[${this.userInformation.roles.join(',')}]`;
    }
  }

  toggleRoles() {
    this.showRoles = !this.showRoles;
  }
}
