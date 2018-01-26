import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Task } from '../model/task';
import { RestConnectorService } from '../services/rest-connector.service';
import { Workbasket } from '../model/workbasket';
import { SafeResourceUrl, DomSanitizer} from '@angular/platform-browser';


@Component({
  selector: 'app-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.scss']
})
export class TaskComponent implements OnInit {

  task: Task = null;
  link: SafeResourceUrl = this.sanitizer.bypassSecurityTrustResourceUrl('https://duckduckgo.com/?q=');
  autoCompleteData: string[] = new Array;
  workbasket: string = null;
  workbasketKey: string;
  workbaskets: Workbasket[];

  private sub: any;

  constructor(private restConnectorService: RestConnectorService,
              private route: ActivatedRoute, private router: Router,
              private sanitizer: DomSanitizer) { }

  ngOnInit() {
        const id = this.route.snapshot.params['id'];
        this.restConnectorService.getTask(id).subscribe(
                       t =>  {
                         this.task = t;
                         this.link = this.sanitizer.bypassSecurityTrustResourceUrl('https://duckduckgo.com/?q=' + this.task.name );
                         this.restConnectorService.getAllWorkBaskets().subscribe( w => {
                            this.workbaskets = w;
                            this.workbaskets.forEach(workbasket => {
                            if (workbasket.key !== this.task.workbasket) {
                              this.autoCompleteData.push(workbasket.name);
                            }
                          });
                       });
    });
  }

  transferTask() {
    if (this.workbasket) {
      this.workbaskets.forEach(workbasket => {
        if (workbasket.name === this.workbasket) {
          this.workbasketKey = workbasket.key;
        }
      });
      this.restConnectorService.transferTask(this.task.id, this.workbasketKey).subscribe(
                       task => {this.task = task});
      this.router.navigate(['tasks/']);
    }
  }

    cancelTask() {
      this.router.navigate(['tasks/']);
    }

    completeTask() {
      this.restConnectorService.completeTask(this.task.id).subscribe(
                       task => {this.task = task});
      this.router.navigate(['tasks/']);
    }
}
