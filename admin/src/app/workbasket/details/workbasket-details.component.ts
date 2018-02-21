import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Workbasket } from '../../model/workbasket';
import { WorkbasketService } from '../../services/workbasketservice.service'
import { MasterAndDetailService } from '../../services/master-and-detail.service'
import { ActivatedRoute, Params, Router, NavigationStart } from '@angular/router';
import { PermissionService } from '../../services/permission.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'workbasket-details',
  templateUrl: './workbasket-details.component.html',
  styleUrls: ['./workbasket-details.component.scss']
})
export class WorkbasketDetailsComponent implements OnInit {

  selectedId: number = -1;
  workbasket: Workbasket;
  workbasketClone: Workbasket;
  showDetail: boolean =  false;
  hasPermission: boolean = true;
  requestInProgress: boolean =  false;

  private workbasketSelectedSubscription: Subscription;
  private workbasketSubscription: Subscription;
  private routeSubscription: Subscription;
  private masterAndDetailSubscription: Subscription;
  private permissionSubscription: Subscription;

  constructor(private service: WorkbasketService,
              private route: ActivatedRoute,
              private router: Router,
              private masterAndDetailService: MasterAndDetailService,
              private permissionService: PermissionService) { }

  

  ngOnInit() {
    this.workbasketSelectedSubscription = this.service.getSelectedWorkBasket().subscribe( workbasketIdSelected => {
      this.workbasket = undefined;
      this.requestInProgress = true;
      this.workbasketSubscription = this.service.getWorkBasket(workbasketIdSelected).subscribe( workbasket => {
        this.workbasket = workbasket;
        this.workbasketClone = { ...this.workbasket };
        this.requestInProgress = false;
       });
    });
    
    this.routeSubscription = this.route.params.subscribe(params => {
      let id = params['id'];
      if( id && id !== '') {
        this.selectedId = id;
        this.service.selectWorkBasket(id);
      }
    });
    
    this.masterAndDetailSubscription = this.masterAndDetailService.getShowDetail().subscribe(showDetail => {
      this.showDetail = showDetail;
    });

    this.permissionSubscription = this.permissionService.hasPermission().subscribe( permission => {
      this.hasPermission = permission;
      if(!this.hasPermission){
        this.requestInProgress = false;
      }
    })

  }

  onSave() {
  }
  
  backClicked(): void {
    this.service.selectWorkBasket(undefined);
    this.router.navigate(['./'], { relativeTo: this.route.parent });
  }

  ngOnDestroy(){
    if(this.workbasketSelectedSubscription){this.workbasketSelectedSubscription.unsubscribe();}
    if(this.workbasketSubscription){this.workbasketSubscription.unsubscribe();}
    if(this.routeSubscription){this.routeSubscription.unsubscribe();}
    if(this.masterAndDetailSubscription){this.masterAndDetailSubscription.unsubscribe();}
    if(this.permissionSubscription){this.permissionSubscription.unsubscribe();}
  }
}
