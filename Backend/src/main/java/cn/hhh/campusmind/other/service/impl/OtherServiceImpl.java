package cn.hhh.campusmind.other.service.impl;

import cn.hhh.campusmind.common.exceptions.OssException;
import cn.hhh.campusmind.common.properties.AliOssProperties;
import cn.hhh.campusmind.common.utils.UserContext;
import cn.hhh.campusmind.other.domain.vo.OssStsVO;
import cn.hhh.campusmind.other.service.IOtherService;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtherServiceImpl implements IOtherService {

    @Value("${hhh-campus-mind.sts.role-arn}")
    private String roleArn;
    @Value("${hhh-campus-mind.sts.access-key-id}")
    private String accessKeyId;
    @Value("${hhh-campus-mind.sts.access-key-secret}")
    private String accessKeySecret;

    private final AliOssProperties aliOssProperties;
    private static final String WORKDIR = "file";
    @Override
    public OssStsVO issueUploadSts(String bizType) {

        Long userId = UserContext.getUserId();

        String dir = null;
        if (WORKDIR.equals(bizType)) {
            dir = "uploads/file/" + userId + "/";
        }
        String roleSessionName = "u-" + userId;

        String policy = String.format(
                "{\"Version\":\"1\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":[\"oss:PutObject\",\"oss:AbortMultipartUpload\",\"oss:ListParts\",\"oss:CompleteMultipartUpload\"],\"Resource\":[\"acs:oss:*:*:%s/%s*\"]}]}",
                aliOssProperties.getBucketName(),
                dir
        );


        DefaultProfile profile = DefaultProfile.getProfile(
                "ap-southeast-1",
                accessKeyId,
                accessKeySecret
        );
        DefaultAcsClient client = new DefaultAcsClient(profile);
        AssumeRoleRequest request = new AssumeRoleRequest();
        request.setSysMethod(com.aliyuncs.http.MethodType.POST);
        request.setRoleArn(roleArn);
        request.setRoleSessionName(roleSessionName);
        request.setDurationSeconds(3600L); // 1小时有效期
        request.setPolicy(policy);
        AssumeRoleResponse response = null;
        try {
            response = client.getAcsResponse(request);
        } catch (ClientException e) {
            throw new OssException(e.getMessage());
        }
        OssStsVO vo = new OssStsVO();
        vo.setAccessKeyId(response.getCredentials().getAccessKeyId());
        vo.setAccessKeySecret(response.getCredentials().getAccessKeySecret());
        vo.setSecurityToken(response.getCredentials().getSecurityToken());
        vo.setExpiration(response.getCredentials().getExpiration());
        vo.setRegion("ap-southeast-1");
        vo.setBucket(aliOssProperties.getBucketName());
        vo.setEndpoint(aliOssProperties.getEndpoint());
        vo.setDir(dir);
        return vo;
    }
}
