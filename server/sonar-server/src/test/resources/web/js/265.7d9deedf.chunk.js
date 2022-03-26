webpackJsonp([265],{2139:function(e,t,n){"use strict";function r(e){var t=e.customOrganizations,r=e.user;return a.a.createElement("div",{className:"account-body account-container"},a.a.createElement("div",{className:"spacer-bottom"},n.i(p.translate)("login"),": ",a.a.createElement("strong",{id:"login"},r.login)),!r.local&&"sonarqube"!==r.externalProvider&&a.a.createElement("div",{id:"identity-provider",className:"spacer-bottom"},a.a.createElement(c.a,{user:r})),!!r.email&&a.a.createElement("div",{className:"spacer-bottom"},n.i(p.translate)("my_profile.email"),": ",a.a.createElement("strong",{id:"email"},r.email)),!t&&f,!t&&a.a.createElement(s.a,{groups:r.groups}),m,a.a.createElement(u.a,{user:r,scmAccounts:r.scmAccounts}))}Object.defineProperty(t,"__esModule",{value:!0});var o=n(0),a=n.n(o),i=n(10),c=n(2430),s=n(2431),u=n(2432),l=n(11),p=n(2),f=a.a.createElement("hr",{className:"account-separator"}),m=a.a.createElement("hr",{className:"account-separator"}),y=function(e){return{customOrganizations:n.i(l.areThereCustomOrganizations)(e),user:n.i(l.getCurrentUser)(e)}};t.default=n.i(i.connect)(y)(r)},2430:function(e,t,n){"use strict";function r(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function o(e,t){if(!e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return!t||"object"!=typeof t&&"function"!=typeof t?e:t}function a(e,t){if("function"!=typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function, not "+typeof t);e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,enumerable:!1,writable:!0,configurable:!0}}),t&&(Object.setPrototypeOf?Object.setPrototypeOf(e,t):e.__proto__=t)}var i=n(0),c=n.n(i),s=n(205),u=function(){function e(e,t){for(var n=0;n<t.length;n++){var r=t[n];r.enumerable=r.enumerable||!1,r.configurable=!0,"value"in r&&(r.writable=!0),Object.defineProperty(e,r.key,r)}}return function(t,n,r){return n&&e(t.prototype,n),r&&e(t,r),t}}(),l=function(e){function t(){var e,n,a,i;r(this,t);for(var c=arguments.length,s=Array(c),u=0;u<c;u++)s[u]=arguments[u];return n=a=o(this,(e=t.__proto__||Object.getPrototypeOf(t)).call.apply(e,[this].concat(s))),a.state={loading:!0},i=n,o(a,i)}return a(t,e),u(t,[{key:"componentDidMount",value:function(){this.mounted=!0,this.fetchIdentityProviders()}},{key:"componentDidUpdate",value:function(e){e.user!==this.props.user&&this.this.fetchIdentityProviders()}},{key:"componentWillUnmount",value:function(){this.mounted=!1}},{key:"fetchIdentityProviders",value:function(){var e=this;this.setState({loading:!0}),n.i(s.d)().then(function(e){return e.identityProviders}).then(function(t){if(e.mounted){var n=t.find(function(t){return t.key===e.props.user.externalProvider});e.setState({loading:!1,identityProvider:n})}}).catch(function(){e.mounted&&e.setState({loading:!1})})}},{key:"render",value:function(){var e=this.props.user,t=this.state,n=t.loading,r=t.identityProvider;return n?null:r?c.a.createElement("div",{className:"identity-provider",style:{backgroundColor:r.backgroundColor}},c.a.createElement("img",{src:window.baseUrl+r.iconPath,width:"14",height:"14",alt:r.name})," ",e.externalIdentity):c.a.createElement("div",null,e.externalProvider,": ",e.externalIdentity)}}]),t}(c.a.PureComponent);t.a=l},2431:function(e,t,n){"use strict";function r(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function o(e,t){if(!e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return!t||"object"!=typeof t&&"function"!=typeof t?e:t}function a(e,t){if("function"!=typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function, not "+typeof t);e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,enumerable:!1,writable:!0,configurable:!0}}),t&&(Object.setPrototypeOf?Object.setPrototypeOf(e,t):e.__proto__=t)}var i=n(0),c=n.n(i),s=n(6),u=n.n(s),l=n(2),p=function(){function e(e,t){for(var n=0;n<t.length;n++){var r=t[n];r.enumerable=r.enumerable||!1,r.configurable=!0,"value"in r&&(r.writable=!0),Object.defineProperty(e,r.key,r)}}return function(t,n,r){return n&&e(t.prototype,n),r&&e(t,r),t}}(),f=function(e){function t(){return r(this,t),o(this,(t.__proto__||Object.getPrototypeOf(t)).apply(this,arguments))}return a(t,e),p(t,[{key:"render",value:function(){var e=this.props.groups;return c.a.createElement("div",null,c.a.createElement("h2",{className:"spacer-bottom"},n.i(l.translate)("my_profile.groups")),c.a.createElement("ul",{id:"groups"},e.map(function(e){return c.a.createElement("li",{key:e,className:"little-spacer-bottom",title:e},e)})))}}]),t}(c.a.PureComponent);f.propTypes={groups:u.a.arrayOf(u.a.string).isRequired},t.a=f},2432:function(e,t,n){"use strict";function r(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function o(e,t){if(!e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return!t||"object"!=typeof t&&"function"!=typeof t?e:t}function a(e,t){if("function"!=typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function, not "+typeof t);e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,enumerable:!1,writable:!0,configurable:!0}}),t&&(Object.setPrototypeOf?Object.setPrototypeOf(e,t):e.__proto__=t)}var i=n(0),c=n.n(i),s=n(6),u=n.n(s),l=n(2),p=function(){function e(e,t){for(var n=0;n<t.length;n++){var r=t[n];r.enumerable=r.enumerable||!1,r.configurable=!0,"value"in r&&(r.writable=!0),Object.defineProperty(e,r.key,r)}}return function(t,n,r){return n&&e(t.prototype,n),r&&e(t,r),t}}(),f=function(e){function t(){return r(this,t),o(this,(t.__proto__||Object.getPrototypeOf(t)).apply(this,arguments))}return a(t,e),p(t,[{key:"render",value:function(){var e=this.props,t=e.user,r=e.scmAccounts;return c.a.createElement("div",null,c.a.createElement("h2",{className:"spacer-bottom"},n.i(l.translate)("my_profile.scm_accounts")),c.a.createElement("ul",{id:"scm-accounts"},c.a.createElement("li",{className:"little-spacer-bottom text-ellipsis",title:t.login},t.login),t.email&&c.a.createElement("li",{className:"little-spacer-bottom text-ellipsis",title:t.email},t.email),r.map(function(e){return c.a.createElement("li",{key:e,className:"little-spacer-bottom",title:e},e)})))}}]),t}(c.a.PureComponent);f.propTypes={user:u.a.object.isRequired,scmAccounts:u.a.arrayOf(u.a.string).isRequired},t.a=f}});
//# sourceMappingURL=265.7d9deedf.chunk.js.map