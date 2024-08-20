import { Component, OnInit } from '@angular/core';
import { KadaiEngineService } from 'app/shared/services/kadai-engine/kadai-engine.service';
import { UserInfo } from 'app/shared/models/user-info';
import { expandDown } from '../../animations/expand.animation';

@Component({
  selector: 'kadai-shared-user-information',
  templateUrl: './user-information.component.html',
  styleUrls: ['./user-information.component.scss'],
  animations: [expandDown]
})
export class UserInformationComponent implements OnInit {
  userInformation: UserInfo;
  roles = '';
  showRoles = false;
  constructor(private kadaiEngineService: KadaiEngineService) {}

  ngOnInit() {
    this.userInformation = this.kadaiEngineService.currentUserInfo;
    if (this.userInformation) {
      this.roles = `[${this.userInformation.roles.join(',')}]`;
    }
  }

  toggleRoles() {
    this.showRoles = !this.showRoles;
  }
}
