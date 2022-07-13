/*
  Copyright (c) 2019 Sogou, Inc.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

  Authors: Wu Jiaxu (wujiaxu@sogou-inc.com)
           Xie Han (xiehan@sogou-inc.com)
*/

#include <stdint.h>
#include <chrono>
#include "DnsCache.h"

#define GET_CURRENT_SECOND	std::chrono::duration_cast<std::chrono::seconds>(std::chrono::steady_clock::now().time_since_epoch()).count()

#define CONFIDENT_INC		10
#define	TTL_INC				10

const DnsCache::DnsHandle *DnsCache::get_inner(const HostPort& host_port, int type)
{
	int64_t cur_time = GET_CURRENT_SECOND;
	std::lock_guard<std::mutex> lock(mutex_);
	const DnsHandle *handle = cache_pool_.get(host_port);

	if (handle)
	{
		switch (type)
		{
		case GET_TYPE_TTL:
			if (cur_time > handle->value.expire_time)
			{
				const_cast<DnsHandle *>(handle)->value.expire_time += TTL_INC;
				cache_pool_.release(handle);
				return NULL;
			}

			break;

		case GET_TYPE_CONFIDENT:
			if (cur_time > handle->value.confident_time)
			{
				const_cast<DnsHandle *>(handle)->value.confident_time += CONFIDENT_INC;
				cache_pool_.release(handle);
				return NULL;
			}

			break;

		default:
			break;
		}
	}

	return handle;
}

