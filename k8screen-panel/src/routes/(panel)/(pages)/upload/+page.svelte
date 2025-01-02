<script lang="ts">
  import type { ConfigItem } from '$lib/model/config/ConfigItem';
  import { configAPI } from '$lib/service/config-service';
  import { getAccessToken } from '$lib/service/storage-manager';
  import { onMount } from 'svelte';

  let file: any;
  let configs: ConfigItem[];

  const uploadFile = async () => {
    const formData = new FormData();
    formData.append('config', file[0]);

    const response = await fetch('http://localhost:8080/api/v1/configs/upload', {
      method: 'POST',
      body: formData,
      headers: {
        Authorization: `Bearer ${getAccessToken()}`
      }
    });

    if (response.ok) {
      console.log('Dosya başarıyla yüklendi!');
    } else {
      console.error('Dosya yüklenemedi!');
    }
  };

  onMount(() => {
    configAPI.getAllConfigs().then((data: ConfigItem[]) => {
      configs = data;
    });
  });
</script>

<input type="file" bind:files={file} />
<button on:click={uploadFile}>Yükle</button>
