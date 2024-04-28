import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubscribeSuccessComponent } from './subscribe-success.component';

describe('SubscribeSuccessComponent', () => {
  let component: SubscribeSuccessComponent;
  let fixture: ComponentFixture<SubscribeSuccessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SubscribeSuccessComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SubscribeSuccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
