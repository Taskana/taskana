import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { WorkplaceComponent } from './workplace.component'
import { TasksComponent } from 'app/workplace/tasks/tasks.component';
import { TaskComponent } from 'app/workplace/task/task.component';

const routes: Routes = [
    {
        path: '',
        component: WorkplaceComponent,
        redirectTo: 'tasks',
        pathMatch: 'full'
    },
    {
        path: '',
        component: WorkplaceComponent,
        children: [
            {
                path: 'tasks',
                component: TasksComponent
            },
            {
                path: 'tasks/:id',
                component: TaskComponent
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class WorkplaceRoutingModule { }
