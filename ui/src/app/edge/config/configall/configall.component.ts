import { Component } from '@angular/core';

import { AbstractConfigComponent } from '../../../shared/config/abstractconfig.component';
import { ConfigImpl } from '../../../shared/edge/config';

@Component({
  selector: 'configall',
  templateUrl: '../../../shared/config/abstractconfig.component.html'
})
export class ConfigAllComponent extends AbstractConfigComponent {
}