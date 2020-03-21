# 클래스 방식의 상속


```
var john = new Person();
```  
생성자 함수와 new 연산자로 클래스 관점에서 객체를 생성한다.  

클래스 방식의 상속을 구현할 때의 목표는 Child()라는 생성자 함수로 생성된 객체들이 Parent()의 프로퍼티를 가지도록 하는 것이다.

```
// 부모 생성자
function Parent(name) {
    this.name = name || 'John';
}

// 생성자의 프로토타입에 기능 추가
Parent.prototype.say = function () {
    return this.name;
}

// 자식 생성자
function Child(name) {}

// 상속
inherit(Child, Parent);
```
inherit() 함수에서 상속을 처리한다. 이제 이 함수의 구현 방법을 살펴본다.

---

## 기본 패턴
Parent() 생성자를 사용해 객체를 생성한 다음, 이 객체를 Child()의 프로토타입에 할당한다.
```
function inherit(C, P) {
    C.prototype = new P();
}
```
프로토타입이 부모 생성자 함수가 아니라 부모 생성자 함수로 생성한 객체 인스턴스를 가리켜야 한다.
```
var kid = new Child();
kid.say(); //John
```
`new Child()`로 생성된 객체는 프로토타입을 통해 Parent() 인스턴스의 기능을 물려받는다.

#### 프로토타입 체인 추적  
kid.name 값을 지정하면 new Parent() 객체의 프로퍼티 값을 변경하는 게 아니라 kid 객체에서 직접 자신의 프로퍼티를 생성한다. `kid.say()`를 실행하면 new Child() - new Parent() - Parent.prototype 순으로 프로퍼티를 탐색한다.  

```
var kid = new Child();
kid.name = "Mike";
kid.say(); //Mike
```

#### 단점
- 부모 객체의 프로퍼티와 프로토타입의 프로퍼티를 모두 물려받는다. 대부분의 경우 객체 자신의 프로퍼티는 특정 인스턴스에 한정되어 재사용할 수 없기 때문에 필요가 없다. (*재사용 가능한 멤버는 프로토타입에 추가해야 한다.)
- 범용 inherit() 함수는 인자를 처리하지 못한다.  
``` 
var s = new Child('Seth');
s.say(); //John 
```

## 생성자 빌려쓰기 패턴
부모 생성자 함수의 this에 자식 객체를 바인딩한 다음, 자식 생성자가 받은 인자들을 모두 넘겨준다.  
이 패턴은 자식에서 부모로 인자를 전달하지 못했던 위의 기본 패턴 문제를 해결한다.
```
function Child(a, b, c, d) {
    Parent.apply(this, arguments);
}
```
이렇게 하면 부모 생성자 함수 내부의 this에 추가된 프로퍼티만 물겨받게 된다. 프로토타입에 추가된 멤버는 상속되지 않는다.  
일반 패턴에서 자식 객체가 상속된 멤버의 참조를 물려받은 것과는 다르게, 자식 객체는 상속된 멤버의 복사본을 받게 된다.
```
// 부모 생성자
function Article() {
    this.tags = ['js', 'css'];
}

var article = new Article();

// 일반 패턴을 사용해 article 객체를 상속하는 blog 객체를 생성한다.
function BlogPost() {}
BlogPost.prototype = article;
var blog = new BlogPost();

// 생성자 빌려쓰기 패턴을 사용해 article을 상속하는 page 객체를 생성한다.
function StaticPage() {
    Artical.call(this);
}
var page = new StaticPage();


console.log(article.hasOwnProperty('tags')); //true
console.log(blog.hasOwnProperty('tags')); //false 
// blog객체는 tags를 자신의 프로퍼티로 가진 것이 아니라 프로토타입을 통해 접근한다.
console.log(page.hasOwnProperty('tags')); //true
// page객체는 부모의 tags에 대한 참조를 얻은 게 아니라 복사본을 얻게 되므로 자신의 tags 프로퍼티를 가진다.

blog.tags.push('html');
page.tags.push('php');
console.log(artigle.tags.join(', ')); // js, css, html
```

#### 프로토타입 체인
```
// 부모 생성자
function Parent(name) {
    this.name = name || 'John';
}

// 프로토타입에 기능을 추가
Parent.prototype.say = function () {
    return this.name;
}

// 자식 생성자
function Child(name) {
    Parent.apply(this, arguments);
}

var kid = new Child('Mike');
kid.name; //Mike
typeof kid.say; //undefined
```
Child 객체와 Parent 사이에 링크는 존재하지 않는다. Child.prototype은 전혀 사용되지 않았기 때문에 그냥 빈 객체를 가리킨다.  
이 패턴을 사용하면 kid는 자기 자신의 name 프로퍼티만 가지고, say() 메서드는 상속받지 않는다. 이 상속은 부모가 가진 프로퍼티를 자식의 프로퍼티로 복사해주는 일회성 동작이며, \__proto\__ 링크는 유지되지 않는다.

#### 다중 상속
생성자 빌려쓰기 패턴으로 생성자를 하나 이상 빌려쓰는 다중 상속을 구현할 수 있다.
```
function Cat() {
    this.legs = 4;
    this.say = function () {
        return "meaowww";
    };
}

function Bird() {
    this.legs = 2;
    this.wings = 2;
    this.fly = true;
}

function CatWings() {
    Cat.apply(this);
    Bird.apply(this);
}

var jane = new CatWings();
console.dir(jane); // fly:true, legs:2, wings:2, say:function()
```
중복 프로퍼티가 존재한다면 마지막 프로퍼티 값으로 덮어쓴다.

#### 장점
- 부모 생성자 자신의 멤버에 대한 복사본을 가져올 수 있다. 자식이 실수로 부모의 프로퍼티를 덮어쓸 위험이 없다.

#### 단점
- 프로토타입이 전혀 상속되지 않는다.


## 생성자 빌려쓰기 + 프로토타입 지정 패턴

앞의 두 패턴을 결합한 것이다. 부모 생성자를 빌려온 후, 자식의 프로토타입이 부모 생성자를 통해 생성된 인스턴스를 가리키도록 지정한다.  
```
function Child() {
    Parent.apply(this, arguments);
}

Child.prototype = new Parent();
```
자식 객체는 부모가 가진 자신만의 프로퍼티의 *복사본*을 가지게 되는 동시에, 부모의 프로토타입 멤버로 구현된 재사용 가능한 기능들에 대한 참조를 물려받는다. 부모 생성자에 인자를 넘길 수도 있다.

```
// 부모 생성자
function Parent(name) {
    this.name = name || 'John';
}

// 프로퍼티에 기능 추가
Parent.prototype.say = function () {
    return this.name;
};

// 자식 생성자
function Child(name) {
    Parent.apply(this, arguments);
}

Child.prototype = new Parent();

var kid = new Child("Mike");
kid.name; //Mike
kid.say(); //Mike
delete kid.name;
kid.say(); //John
```

#### 단점
- 부모 생성자를 비효율적으로 두 번 호출한다. 부모가 가진 자신만의 프로퍼티를 두 번 상속된다.

## 프로토타입 공유 패턴
원칙적으로 재사용할 멤버는 this가 아니라 프로토타입에 추가되어야 한다.
```
function inherit(C, P) {
    C.prototype = P.prototype;
}
```
#### 단점
- 상속 체인의 하단 어딘가에서 프로토타입을 수정할 경우 모든 자식의 객체에 영향을 미친다.

## 임시 생성자 패턴
빈 함수 F()가 부모와 자식 사이에서 프록시 기능을 맡는다. F()의 프로토타입은 부모의 프로토타입을 가리킨다. 이 빈 함수의 인스턴스가 자식의 프로토타입이 된다.  
부모와 자식의 프로토타입 사이에 직접적인 링크를 끊는다. 자식이 프로토타입의 프로퍼티만을 물려받는다. 부모 생성자에서 this에 추가한 멤버는 상속되지 않는다.
```
// 부모 생성자
function Parent(name) {
    this.name = name || 'John';
}

// 프로퍼티에 기능 추가
Parent.prototype.say = function () {
    return this.name;
};

// 자식 생성자
function Child() {}

// 상속
inherit(Child, Parent);

function inherit(C, P) {
    var F = function () {};
    F.prototype = P.prototype;
    C.prototype = new F();
}

var kid = new Child();
kid.name; //undefined
```
name은 부모 자신의 프로퍼티인데, 상속 과정에서 new Parent()를 호출한 적이 없기 때문에 이 프로퍼티는 생성조차 되지 않았다.  

#### 상위 클래스 저장
```
function inherit(C, P) {
    var F = function () {};
    F.prototype = P.prototype;
    C.prototype = new F();
    C.uber = P.prototype;
}
```
부모 원본에 대한 참조를 추가할 수 있다. 다른 언어에서 상위 클래스에 대한 접근 경로를 가지는 것과 같은 기능이다.

#### 생성자 포인터 재설정
생성자 포인터를 재설정하지 않으면 모든 자식 객체들의 생성자는 Parent()로 지정되어 있을 것이다.
```
// 부모와 자식을 두고 상속관계를 만든다
function Parent() {}
function Child() {}
inherit(Child, Parent);

// 생성자를 확인해본다
var kid = new Child();
kid.constructor.name; //Parent
kid.constructor === Parent; //true

function inherit(C, P) {
    var F = function () {};
    F.prototype = P.prototype;
    C.prototype = new F();
    C.uber = P.prototype;
}
```
아래와 같이 생성자를 Child()로 지정하여 constructor 프로퍼티를 런타임 객체 판별에 사용할 수 있도록 한다. 
```
// 부모와 자식을 두고 상속관계를 만든다
function Parent() {}
function Child() {}
inherit(Child, Parent);

// 생성자를 확인해본다
var kid = new Child();
kid.constructor.name; //Child
kid.constructor === Parent; //false

function inherit(C, P) {
    var F = function() {};
    F.prototype = P.prototype;
    C.prototype = new F();
    C.uber = P.prototype;
    C.prototype.constuctor = C;
}
```