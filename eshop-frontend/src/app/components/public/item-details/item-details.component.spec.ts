import { ComponentFixture, TestBed, inject } from '@angular/core/testing';

import { ItemDetailsComponent } from './item-details.component';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { ItemService } from '../../../services/item.service';
import { By } from '@angular/platform-browser';

describe('ItemDetailsComponent', () => {
  let component: ItemDetailsComponent;
  let fixture: ComponentFixture<ItemDetailsComponent>;
  let route: ActivatedRoute;

  beforeEach(async () => {

    let testItem = {
      id: 1, name: "a1", price: 1, category: "",
      description: ""
    };
    const itemService = jasmine.createSpyObj('ItemService', ['getItem']);
    let getItemsSpy = itemService.getItem.and.returnValue(of(testItem));

    await TestBed.configureTestingModule({
      declarations: [ItemDetailsComponent],
      imports: [RouterModule.forRoot([])],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ItemService, useValue: itemService }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ItemDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  beforeEach(() => {
    route = TestBed.inject(ActivatedRoute);
    spyOn(route.snapshot.paramMap, 'get').and.returnValue('1'); //itemID = 1

    fixture = TestBed.createComponent(ItemDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display selected item', () => {
    expect(component.item.name).toEqual('a1');
  });

  it('should add item to cart and navigate to cart page',
    inject([Router], (router: Router) => {
      spyOn(router, 'navigate').and.stub();

      const addToCartButton: HTMLElement
        = fixture.debugElement.query(By.css('#addToCart')).nativeElement;
      addToCartButton.dispatchEvent(new Event('click'));
      fixture.detectChanges();
      expect(component.storeService.cart.cartItems.length).toEqual(1);
      expect(router.navigate).toHaveBeenCalledWith(['/cart']);
    }));
});