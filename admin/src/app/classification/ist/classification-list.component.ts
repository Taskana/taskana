import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {ClassificationService} from '../../services/classification.service';

@Component({
  selector: 'taskana-classification-list',
  templateUrl: './classification-list.component.html',
  styleUrls: ['./classification-list.component.scss']
})
export class ClassificationListComponent implements OnInit {

  requestInProgress = false;

  constructor(private route: ActivatedRoute, private router: Router, private httpClient: HttpClient,
              private classificationService: ClassificationService) {
  }

  ngOnInit() {
  }
}
